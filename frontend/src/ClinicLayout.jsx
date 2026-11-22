import { NavLink, Outlet, useLocation } from "react-router-dom";
import { clearSession } from "./services/api";
import { modules } from "./modules";

export default function ClinicLayout() {
  const { pathname } = useLocation();
  const section = pathname.split("/")[1];
  return (
    <div className="clinic">
      <a className="skip-link" href="#conteudo">Ir para o conteúdo</a>
      <aside className="clinic-sidebar">
        <NavLink className="clinic-brand" to="/dashboard">
          <span aria-hidden="true" className="clinic-mark">
            <svg viewBox="0 0 64 64" width="22" height="22">
              <path d="M18 7c6-2 9 2 14 2s8-4 14-2c9 3 11 14 7 24-3 7-5 22-10 25-6 3-5-15-11-15S27 59 21 56c-5-3-7-18-10-25C7 21 9 10 18 7Z"
                fill="none" stroke="currentColor" strokeWidth="4" strokeLinejoin="round" />
            </svg>
          </span>
          Thay e Theu
        </NavLink>
        <nav aria-label="Menu principal">
          <NavLink to="/dashboard">Dashboard</NavLink>
          {Object.entries(modules).map(([key, module]) =>
            <NavLink key={key} to={`/${key}`}>{module.title}</NavLink>
          )}
        </nav>
      </aside>
      <div className="clinic-body">
        <header className="clinic-header">
          <strong>{modules[section]?.title ?? "Dashboard"}</strong>
          <button type="button" className="button secondary" onClick={clearSession}>Sair</button>
        </header>
        <main id="conteudo" tabIndex={-1} className="clinic-content"><Outlet /></main>
      </div>
    </div>
  );
}
