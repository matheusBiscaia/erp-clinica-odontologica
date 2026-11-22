import { useState } from "react";
import api, { saveSession } from "../services/api";
import "./Login.css";

function Login() {
  const [email, setEmail] = useState("");
  const [senha, setSenha] = useState("");
  const [mensagem, setMensagem] = useState("");
  const [carregando, setCarregando] = useState(false);
  const [lembrar, setLembrar] = useState(false);

  async function fazerLogin(event) {
    event.preventDefault();
    setMensagem("");
    setCarregando(true);

    try {
      const resposta = await api.post("/api/auth/login", {
        email,
        senha,
      });
      saveSession(resposta.data.token, lembrar);
    } catch (error) {
      setSenha("");
      setMensagem([400, 401, 403].includes(error.response?.status)
        ? "E-mail ou senha inválidos."
        : "Não foi possível entrar. Tente novamente.");
    } finally {
      setCarregando(false);
    }
  }

  return (
    <main className="login-page">
      <div className="decoracao decoracao-um" />
      <div className="decoracao decoracao-dois" />

      <section className="login-container">
        <aside className="login-presentation">
          <div className="brand">
            <div className="brand-icon" aria-hidden="true">
              <svg viewBox="0 0 64 64">
                <path
                  d="M18 7c6-2 9 2 14 2s8-4 14-2c9 3 11 14 7 24-3 7-5 22-10 25-6 3-5-15-11-15S27 59 21 56c-5-3-7-18-10-25C7 21 9 10 18 7Z"
                  fill="none"
                  stroke="currentColor"
                  strokeWidth="4"
                  strokeLinejoin="round"
                />
              </svg>
            </div>

            <div>
              <strong>Thay e Theu</strong>
              <span>Gestão inteligente</span>
            </div>
          </div>

          <div className="presentation-content">
            <span className="presentation-tag">Gestão odontológica</span>

            <h1>Cuide da sua clínica em um só lugar.</h1>

            <p>
              Gerencie pacientes, profissionais, consultas, prontuários e
              pagamentos com segurança e simplicidade.
            </p>

            <ul>
              <li><span>✓</span> Informações centralizadas</li>
              <li><span>✓</span> Rotinas mais organizadas</li>
              <li><span>✓</span> Acesso seguro e protegido</li>
            </ul>
          </div>

          <p className="presentation-footer">
            ERP para clínicas odontológicas
          </p>
        </aside>

        <div className="login-content">
          <div className="mobile-brand">
            <div className="brand-icon">O</div>
            <strong>Thay e Theu</strong>
          </div>

          <header className="login-header">
            <span>Bem-vindo de volta</span>
            <h2>Acesse sua conta</h2>
            <p>Informe suas credenciais para acessar o painel.</p>
          </header>

          <form className="login-form" onSubmit={fazerLogin}>
            <div className="form-group">
              <label htmlFor="email">E-mail</label>

              <div className="input-wrapper">
                <span className="input-icon" aria-hidden="true">✉</span>

                <input
                  id="email"
                  type="email"
                  placeholder="seuemail@clinica.com"
                  value={email}
                  onChange={(event) => setEmail(event.target.value)}
                  autoComplete="email"
                  required
                />
              </div>
            </div>

            <div className="form-group">
              <label htmlFor="senha">Senha</label>

              <div className="input-wrapper">
                <span className="input-icon" aria-hidden="true">⌑</span>

                <input
                  id="senha"
                  type="password"
                  placeholder="Digite sua senha"
                  value={senha}
                  onChange={(event) => setSenha(event.target.value)}
                  autoComplete="current-password"
                  required
                />
              </div>
            </div>

            <div className="form-options">
              <label className="remember">
                <input
                    type="checkbox"
                    checked={lembrar}
                    onChange={(event) => setLembrar(event.target.checked)}
                />
                <span>Lembrar de mim</span>
                </label>

              <button className="forgot-password" type="button" disabled>
                Esqueceu a senha?
              </button> <small>Funcionalidade em desenvolvimento</small>
            </div>

            <button className="login-button" type="submit" disabled={carregando}>
              {carregando ? (
                <>
                  <span className="spinner" />
                  Entrando...
                </>
              ) : (
                <>
                  Entrar
                  <span aria-hidden="true">→</span>
                </>
              )}
            </button>

            {mensagem && (
              <p className="mensagem erro" role="alert">
                {mensagem}
              </p>
            )}
          </form>

          <p className="login-security">
            <span>◆</span> Ambiente protegido e seguro
          </p>
        </div>
      </section>
    </main>
  );
}

export default Login;