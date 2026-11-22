import { useEffect, useState } from "react";
import { BrowserRouter, Navigate, Outlet, Route, Routes } from "react-router-dom";
import ClinicLayout from "./ClinicLayout";
import Dashboard from "./pages/Dashboard";
import { RecordList, RecordPage } from "./pages/Records";
import { modules } from "./modules";
import "./App.css";
import Login from "./pages/Login";
import { clearSession, expiration, storedToken } from "./services/api";

function useSession() {
  const [token, setToken] = useState(() => expiration(storedToken()) > Date.now() ? storedToken() : null);
  useEffect(() => {
    const update = () => setToken(expiration(storedToken()) > Date.now() ? storedToken() : null);
    window.addEventListener("thay-e-theu-session", update);
    window.addEventListener("storage", update);
    return () => {
      window.removeEventListener("thay-e-theu-session", update);
      window.removeEventListener("storage", update);
    };
  }, []);
  useEffect(() => {
    if (!token) { if (storedToken()) clearSession(); return; }
    const remaining = expiration(token) - Date.now();
    if (remaining <= 0) {
      clearSession();
      return;
    }
    const timer = setTimeout(clearSession, Math.min(remaining, 2147483647));
    return () => clearTimeout(timer);
  }, [token]);
  return Boolean(token);
}

export default function App() {
  const authenticated = useSession();
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={authenticated ? <Navigate to="/dashboard" replace /> : <Login />} />
        <Route element={authenticated ? <Outlet /> : <Navigate to="/login" replace />}>
          <Route element={<ClinicLayout />}>
            <Route path="/dashboard" element={<Dashboard />} />
            {Object.keys(modules).map(section => (
              <Route key={section} path={section}>
                <Route index element={<RecordList key={section} section={section} />} />
                <Route path="novo" element={<RecordPage section={section} mode="new" />} />
                <Route path=":id" element={<RecordPage section={section} mode="view" />} />
                <Route path=":id/editar" element={<RecordPage section={section} mode="edit" />} />
              </Route>
            ))}
          </Route>
          <Route path="*" element={<Navigate to="/dashboard" replace />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}
