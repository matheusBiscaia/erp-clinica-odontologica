import { Link } from "react-router-dom";
import { LoadState, Table } from "../ClinicControls";
import { amountFor, modules, orderAppointments } from "../modules";
import api from "../services/api";
import useLoad from "../services/useLoad";

const sections = ["pacientes","dentistas","agendamentos","prontuarios","pagamentos"];
async function loadDashboard(signal) {
  const results = await Promise.allSettled(sections.map(section =>
    api.get(`/api/${section}`, { signal }).then(({ data }) => data)));
  const available = {};
  results.forEach((result, index) => {
    if (result.status === "fulfilled") available[sections[index]] = result.value;
  });
  const upcoming = available.agendamentos && orderAppointments(available.agendamentos)
    .filter(row => ["AGENDADA", "CONFIRMADA"].includes(row.status) && new Date(row.dataHora).getTime() >= Date.now())
    .slice(0, 5);
  const counts = Object.fromEntries(Object.entries(available).map(([section, rows]) => [section, rows.length]));
  const payments = available.pagamentos;
  return {
    counts,
    upcoming: upcoming?.map(({ id, dataHora, dentistaNome, status }) => ({ id, dataHora, dentistaNome, status })),
    pending: payments && {
      count: payments.filter(row => row.status === "PENDENTE").length,
      amount: amountFor(payments, "PENDENTE"),
    },
  };
}

export default function Dashboard() {
  const result = useLoad(loadDashboard);
  const { counts, upcoming, pending } = result.data ?? {};
  const incomplete = counts && Object.keys(counts).length < sections.length;
  return (
    <>
      <div className="page-heading dashboard-heading">
        <div><h1>Visão geral</h1><p>Acompanhe a agenda e os registros da clínica.</p></div>
        <div className="actions">
          <Link className="button secondary" to="/pacientes/novo">Cadastrar paciente</Link>
          <Link className="button" to="/agendamentos/novo">Criar agendamento</Link>
        </div>
      </div>
      <LoadState result={result} />
      {incomplete && <LoadState result={{ ...result, error: "Alguns dados estão indisponíveis." }} />}
      {counts && <>
        <div className="metrics">
          {sections.map(section => <Link className="metric" key={section} to={`/${section}`}>
            <span>{modules[section].title}{["pacientes", "dentistas"].includes(section) && " ativos"}</span>
            <strong>{counts[section] ?? "—"}</strong>
          </Link>)}
        </div>
        <div className="dashboard-grid">
          <section className="panel">
            <h2>Próximos agendamentos</h2>
            {upcoming ? <Table title="Próximos agendamentos" columns={["dataHora", "dentistaNome", "status"]} rows={upcoming} />
              : <p>Agenda indisponível.</p>}
          </section>
          <section className="panel">
            <h2>Pagamentos pendentes</h2>
            {pending ? <dl className="totals">
              <div><dt>Cobranças</dt><dd>{pending.count}</dd></div>
              <div><dt>Valor pendente</dt><dd>{pending.amount}</dd></div>
            </dl> : <p>Pagamentos indisponíveis.</p>}
          </section>
        </div>
      </>}
    </>
  );
}
