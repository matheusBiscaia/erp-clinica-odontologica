import { useCallback, useState } from "react";
import { Link, useLocation, useNavigate, useParams } from "react-router-dom";
import { Dialog, LoadState, Table } from "../ClinicControls";
import { amountFor, display, labels, modules, orderAppointments, paymentMethods, statuses } from "../modules";
import api, { apiError } from "../services/api";
import useLoad from "../services/useLoad";

const endpoint = (section, id) => id ? `/api/${section}/${id}` : `/api/${section}`;
const read = (url, signal) => api.get(url, { signal }).then(response => response.data);
const normalize = value => String(value ?? "").normalize("NFD").replace(/\p{Diacritic}/gu, "").toLowerCase();

function rowActions(section, row) {
  if (modules[section].deactivate) return [{ label: "Inativar", method: "delete" }];
  if (section === "agendamentos") {
    const actions = [];
    if (row.status === "AGENDADA") actions.push({ label: "Confirmar", path: "confirmar" });
    if (["AGENDADA", "CONFIRMADA"].includes(row.status)) actions.push({ label: "Concluir", path: "concluir" });
    if (row.status !== "CANCELADA") actions.push({ label: "Cancelar", path: "cancelar" });
    return actions;
  }
  if (section === "pagamentos" && row.status === "PENDENTE") {
    return [{ label: "Dar baixa", path: "pagar" }, { label: "Cancelar", path: "cancelar" }];
  }
  return [];
}

function ActionDialog({ section, selection, close, saved }) {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState("");
  const { row, action } = selection;

  async function submit(event) {
    event.preventDefault();
    const method = new FormData(event.currentTarget).get("formaPagamento");
    setBusy(true);
    setError("");
    try {
      const url = endpoint(section, row.id);
      if (action.method === "delete") await api.delete(url);
      else await api.patch(`${url}/${action.path}`, method ? { formaPagamento: method } : undefined);
      saved();
    } catch (failure) {
      setError(apiError(failure));
      setBusy(false);
    }
  }

  return (
    <Dialog title={`${action.label} ${modules[section].singular}`} busy={busy} onClose={close}>
      <form onSubmit={submit}>
        <p>Confirme a operação para o registro #{row.id}.</p>
        {action.path === "pagar" && <label className="field">
          Forma de pagamento
          <select name="formaPagamento" required defaultValue="">
            <option value="">Selecione</option>
            {Object.entries(paymentMethods).map(([key, label]) => <option key={key} value={key}>{label}</option>)}
          </select>
        </label>}
        {error && <p className="notice error" role="alert">{error}</p>}
        <div className="actions">
          <button className="button secondary" type="button" disabled={busy} onClick={close}>Voltar</button>
          <button className="button" disabled={busy}>{busy ? "Salvando…" : "Confirmar operação"}</button>
        </div>
      </form>
    </Dialog>
  );
}

export function RecordList({ section }) {
  const module = modules[section];
  const { state } = useLocation();
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("");
  const [selected, setSelected] = useState(null);
  const [notice, setNotice] = useState(state?.notice ?? "");
  const load = useCallback(signal => read(endpoint(section), signal), [section]);
  const result = useLoad(load);
  const all = result.data ?? [];
  const ordered = section === "agendamentos" ? orderAppointments(all) : all;
  const keys = section === "dentistas" ? [...module.columns, "cpf"] : module.columns;
  const search = normalize(query.trim());
  const digits = search.replace(/\D/g, "");
  const numericSearch = digits && /^[\d\s()+.-]+$/.test(search);
  const rows = ordered.filter(row => (!status || row.status === status) &&
    keys.some(key => normalize(display(key, row[key])).includes(search) ||
      (numericSearch && ["cpf", "telefone"].includes(key) &&
        String(row[key] ?? "").replace(/\D/g, "").includes(digits))));

  return (
    <>
      <div className="page-heading">
        <div><h1>{module.title}</h1><p>{module.description}</p></div>
        <Link className="button" to={`/${section}/novo`}>Cadastrar {module.singular}</Link>
      </div>
      {notice && <p className="notice success" role="status">{notice}</p>}
      <LoadState result={result} />
      {result.data && <section className="panel">
        {section === "pagamentos" && <dl className="totals">
          <div><dt>Total recebido</dt><dd>{amountFor(all, "PAGO")}</dd></div>
          <div><dt>Total pendente</dt><dd>{amountFor(all, "PENDENTE")}</dd></div>
        </dl>}
        <div className="filters">
          <label className="field">Buscar
            <input type="search" value={query} onChange={event => setQuery(event.target.value)} />
          </label>
          {module.statuses && <label className="field">Status
            <select value={status} onChange={event => setStatus(event.target.value)}>
              <option value="">Todos</option>
              {module.statuses.map(key => <option key={key} value={key}>{statuses[key]}</option>)}
            </select>
          </label>}
        </div>
        <Table columns={module.columns} rows={rows} title={module.title} actions={row => <>
          <Link to={`/${section}/${row.id}`} aria-label={`Ver registro ${row.id}`}>Ver</Link>
          {(section !== "pagamentos" || row.status === "PENDENTE") &&
            <Link to={`/${section}/${row.id}/editar`} aria-label={`Editar registro ${row.id}`}>Editar</Link>}
          {rowActions(section, row).map(action => <button type="button" key={action.label}
            onClick={() => setSelected({ row, action })}>{action.label}</button>)}
        </>} />
      </section>}
      {selected && <ActionDialog section={section} selection={selected} close={() => setSelected(null)}
        saved={() => {
          setSelected(null);
          setNotice("Operação concluída com sucesso.");
          result.retry();
        }} />}
    </>
  );
}

function localDate(date) {
  return new Date(date.getTime() - date.getTimezoneOffset() * 60000).toISOString();
}

function Field({ field, values, update, options }) {
  const { key, label, source, type, ...constraints } = field;
  const props = { ...constraints, id: key, name: key, value: values[key] ?? "",
    onChange: event => update(key, event.target.value) };
  const [birthDateLimit] = useState(() => {
    if (key !== "dataNascimento") return undefined;
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);
    return localDate(yesterday).slice(0, 10);
  });
  if (birthDateLimit) props.max = birthDateLimit;
  return (
    <label className={`field ${type === "textarea" ? "full-width" : ""}`} htmlFor={key}>
      {label}{props.required && " *"}
      {source ? <select {...props}>
        <option value="">{props.required ? "Selecione" : "Sem agendamento"}</option>
        {options.map(option => <option key={option.id} value={option.id}>
          {option.nomeCompleto ?? `${display("dataHora", option.dataHora)} · ${option.procedimento}`}
        </option>)}
      </select> : type === "textarea" ? <textarea {...props} rows={4} /> :
        <input {...props} type={type ?? "text"} />}
    </label>
  );
}

function RecordForm({ section, id, record, sources }) {
  const module = modules[section];
  const navigate = useNavigate();
  const [values, setValues] = useState(() => Object.fromEntries(module.fields.map(field =>
    [field.key, String(record?.[field.key] ?? "")])));
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  function update(key, value) {
    setValues(previous => ({
      ...previous, [key]: value,
      ...(key === "pacienteId" && section === "pagamentos" ? { agendamentoId: "" } : {}),
    }));
  }

  async function submit(event) {
    event.preventDefault();
    setError("");
    const payload = {};
    for (const field of module.fields) {
      const value = values[field.key].trim();
      if (field.required && !value) {
        setError(`Preencha o campo ${field.label}.`);
        event.currentTarget.elements[field.key].focus();
        return;
      }
      if (field.source && value && !/^[1-9][0-9]*$/.test(value)) {
        setError(`Selecione um registro válido em ${field.label}.`);
        return;
      }
      payload[field.key] = field.source && value ? Number(value) : value || null;
    }
    if (section === "agendamentos" && new Date(payload.dataHora).getTime() < Date.now()) {
      setError("Escolha uma data e um horário futuros.");
      return;
    }
    setBusy(true);
    try {
      if (id) await api.put(endpoint(section, id), payload);
      else await api.post(endpoint(section), payload);
      navigate(`/${section}`, { replace: true, state: { notice: "Registro salvo com sucesso." } });
    } catch (failure) {
      setError(apiError(failure));
      setBusy(false);
    }
  }

  return (
    <form className="panel" onSubmit={submit}>
      <p className="muted">* Campos obrigatórios</p>
      <div className="form-grid">
        {module.fields.map(field => {
          let options = sources[field.source] ?? [];
          if (field.source === "agendamentos") {
            options = options.filter(item => String(item.pacienteId) === values.pacienteId);
          }
          const existing = record?.[field.key];
          if (field.source && existing && String(existing) === values[field.key] &&
              !options.some(item => item.id === existing)) {
            const name = field.source === "pacientes" ? record.pacienteNome : record.dentistaNome;
            options = [...options, { id: existing, nomeCompleto: name ?? `Registro #${existing}` }];
          }
          return <Field key={field.key} field={field} values={values} update={update} options={options} />;
        })}
      </div>
      {error && <p className="notice error" role="alert">{error}</p>}
      <div className="actions">
        <button className="button" disabled={busy}>{busy ? "Salvando…" : "Salvar"}</button>
        {!busy && <Link className="button secondary" to={`/${section}`}>Cancelar</Link>}
      </div>
    </form>
  );
}

export function RecordPage({ section, mode }) {
  const { id } = useParams();
  const module = modules[section];
  const load = useCallback(async signal => {
    const dependencies = mode === "view" ? [] : module.fields.flatMap(field => field.source ? [field.source] : []);
    const [record, sources] = await Promise.all([
      id ? read(endpoint(section, encodeURIComponent(id)), signal) : null,
      Promise.all(dependencies.map(async source => [source, await read(endpoint(source), signal)])),
    ]);
    return { record, sources: Object.fromEntries(sources) };
  }, [section, id, mode, module]);
  const result = useLoad(load);
  const title = { view: "Detalhes de", edit: "Editar", new: "Cadastrar" }[mode];
  const record = result.data?.record;
  return (
    <>
      <div className="page-heading">
        <h1>{title} {module.singular}</h1>
        <Link className="button secondary" to={`/${section}`}>Voltar à lista</Link>
      </div>
      <LoadState result={result} />
      {result.data && (mode === "view" ? <dl className="panel details">
        {module.details.map(key => <div key={key}><dt>{labels[key]}</dt><dd>{display(key, record[key])}</dd></div>)}
      </dl> : <RecordForm key={`${section}:${id ?? "novo"}`} section={section} id={id} {...result.data} />)}
    </>
  );
}
