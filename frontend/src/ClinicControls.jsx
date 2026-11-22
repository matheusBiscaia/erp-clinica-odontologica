import { useEffect, useRef } from "react";
import { display, labels } from "./modules";

export function LoadState({ result }) {
  if (result.loading) return <p role="status">Carregando…</p>;
  if (!result.error) return null;
  return (
    <div className="notice error">
      <p role="alert">{result.error}</p>
      <button className="button secondary" type="button" onClick={result.retry}>Tentar novamente</button>
    </div>
  );
}

export function Table({ columns, rows, actions, title }) {
  if (!rows.length) return <p className="empty">Nenhum registro encontrado.</p>;
  return (
    <div className="table-scroll" role="region" aria-label={title} tabIndex={0}>
      <table aria-label={title}>
        <thead><tr>
          {columns.map(key => <th key={key} scope="col">{labels[key]}</th>)}
          {actions && <th scope="col">Ações</th>}
        </tr></thead>
        <tbody>{rows.map(row => (
          <tr key={row.id}>
            {columns.map(key => <td key={key}>{display(key, row[key])}</td>)}
            {actions && <td><div className="actions">{actions(row)}</div></td>}
          </tr>
        ))}</tbody>
      </table>
    </div>
  );
}

export function Dialog({ title, onClose, busy, children }) {
  const dialog = useRef(null);
  useEffect(() => {
    const element = dialog.current;
    element.showModal();
    return () => element.close();
  }, []);
  return (
    <dialog ref={dialog} aria-labelledby="dialog-title" onCancel={event => {
      event.preventDefault();
      if (!busy) onClose();
    }}>
      <h2 id="dialog-title">{title}</h2>
      {children}
    </dialog>
  );
}
