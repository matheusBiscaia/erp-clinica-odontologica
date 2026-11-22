const name = { key: "nomeCompleto", label: "Nome completo", required: true, maxLength: 150 };
const cpf = { key: "cpf", label: "CPF (11 dígitos)", required: true, pattern: "[0-9]{11}", inputMode: "numeric" };
const phone = { key: "telefone", label: "Telefone", type: "tel", maxLength: 20 };
const email = { key: "email", label: "E-mail", type: "email", maxLength: 150 };
const patient = { key: "pacienteId", label: "Paciente", source: "pacientes", required: true };
const dentist = { key: "dentistaId", label: "Dentista", source: "dentistas", required: true };

export const labels = {
  nomeCompleto: "Nome completo", cpf: "CPF", telefone: "Telefone", email: "E-mail",
  dataNascimento: "Nascimento", ativo: "Cadastro", cro: "CRO", especialidade: "Especialidade",
  pacienteNome: "Paciente", dentistaNome: "Dentista", dataHora: "Data e horário",
  procedimento: "Procedimento", status: "Status", observacoes: "Observações",
  dataAtendimento: "Atendimento", diagnostico: "Diagnóstico", tratamento: "Tratamento",
  descricao: "Descrição", valor: "Valor", dataVencimento: "Vencimento",
  dataPagamento: "Data da baixa", formaPagamento: "Forma de pagamento", agendamentoId: "Agendamento",
};

export const statuses = {
  AGENDADA: "Agendada", CONFIRMADA: "Confirmada", CONCLUIDA: "Concluída", CANCELADA: "Cancelada",
  PENDENTE: "Pendente", PAGO: "Pago", CANCELADO: "Cancelado",
};

export const paymentMethods = {
  DINHEIRO: "Dinheiro", PIX: "Pix", CARTAO_CREDITO: "Cartão de crédito",
  CARTAO_DEBITO: "Cartão de débito", BOLETO: "Boleto",
};

export const modules = {
  pacientes: {
    title: "Pacientes", singular: "paciente", description: "Gerencie os cadastros e contatos da clínica.",
    columns: ["nomeCompleto", "cpf", "telefone", "email"],
    details: ["nomeCompleto", "cpf", "dataNascimento", "telefone", "email", "ativo"],
    fields: [name, cpf, { key: "dataNascimento", label: "Nascimento", type: "date", required: true },
      { ...phone, required: true }, email],
    deactivate: true,
  },
  dentistas: {
    title: "Dentistas", singular: "dentista", description: "Consulte a equipe e suas especialidades.",
    columns: ["nomeCompleto", "cro", "especialidade", "telefone", "email"],
    details: ["nomeCompleto", "cpf", "cro", "especialidade", "telefone", "email", "ativo"],
    fields: [name, cpf, { key: "cro", label: "CRO", required: true, maxLength: 20 },
      { key: "especialidade", label: "Especialidade", required: true, maxLength: 100 }, phone, email],
    deactivate: true,
  },
  agendamentos: {
    title: "Agendamentos", singular: "agendamento", description: "Organize horários e acompanhe as consultas.",
    columns: ["dataHora", "pacienteNome", "dentistaNome", "procedimento", "status"],
    details: ["dataHora", "pacienteNome", "dentistaNome", "procedimento", "status", "observacoes"],
    fields: [patient, dentist, { key: "dataHora", label: "Data e horário", type: "datetime-local", required: true },
      { key: "procedimento", label: "Procedimento", required: true, maxLength: 150 },
      { key: "observacoes", label: "Observações", type: "textarea", maxLength: 500 }],
    statuses: ["AGENDADA", "CONFIRMADA", "CONCLUIDA", "CANCELADA"],
  },
  prontuarios: {
    title: "Prontuários", singular: "prontuário", description: "Consulte o histórico clínico por paciente ou dentista.",
    columns: ["dataAtendimento", "pacienteNome", "dentistaNome"],
    details: ["dataAtendimento", "pacienteNome", "dentistaNome", "diagnostico", "tratamento", "observacoes"],
    fields: [patient, dentist, { key: "dataAtendimento", label: "Data do atendimento", type: "datetime-local", required: true },
      { key: "diagnostico", label: "Diagnóstico", type: "textarea", required: true, maxLength: 1000 },
      { key: "tratamento", label: "Tratamento", type: "textarea", required: true, maxLength: 1000 },
      { key: "observacoes", label: "Observações", type: "textarea", maxLength: 2000 }],
  },
  pagamentos: {
    title: "Pagamentos", singular: "pagamento", description: "Acompanhe cobranças, vencimentos e baixas.",
    columns: ["pacienteNome", "descricao", "valor", "dataVencimento", "status"],
    details: ["pacienteNome", "agendamentoId", "descricao", "valor", "dataVencimento", "status", "formaPagamento", "dataPagamento"],
    fields: [patient, { key: "agendamentoId", label: "Agendamento", source: "agendamentos" },
      { key: "descricao", label: "Descrição", required: true, maxLength: 200 },
      { key: "valor", label: "Valor (R$)", type: "number", required: true, min: "0.01", max: "9999999999.99", step: "0.01" },
      { key: "dataVencimento", label: "Vencimento", type: "date", required: true }],
    statuses: ["PENDENTE", "PAGO", "CANCELADO"],
  },
};

const currency = new Intl.NumberFormat("pt-BR", { style: "currency", currency: "BRL" });
const date = new Intl.DateTimeFormat("pt-BR", { dateStyle: "short" });
const dateTime = new Intl.DateTimeFormat("pt-BR", { dateStyle: "short", timeStyle: "short" });

export function display(key, value) {
  if (value == null || value === "") return "—";
  if (key === "valor") return currency.format(value);
  if (key === "status") return statuses[value] ?? value;
  if (key === "formaPagamento") return paymentMethods[value] ?? value;
  if (key === "ativo") return value ? "Ativo" : "Inativo";
  if (key.startsWith("data")) {
    const parsed = new Date(value.length === 10 ? `${value}T00:00:00` : value);
    if (!Number.isNaN(parsed.getTime())) return (value.length === 10 ? date : dateTime).format(parsed);
  }
  return value;
}

export function amountFor(rows, status) {
  const cents = rows.filter(row => row.status === status)
    .reduce((total, row) => total + Math.round(Number(row.valor) * 100), 0);
  return display("valor", cents / 100);
}

export function orderAppointments(rows) {
  const now = Date.now();
  return [...rows].sort((first, second) => {
    const left = new Date(first.dataHora).getTime();
    const right = new Date(second.dataHora).getTime();
    if (left >= now && right >= now) return left - right;
    return right - left;
  });
}
