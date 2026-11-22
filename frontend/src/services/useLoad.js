import { useEffect, useState } from "react";
import { apiError } from "./api";

export default function useLoad(load) {
  const [revision, setRevision] = useState(0);
  const [result, setResult] = useState(null);
  useEffect(() => {
    const controller = new AbortController();
    load(controller.signal).then(
      data => {
        if (!controller.signal.aborted) setResult({ load, revision, data });
      },
      error => {
        if (!controller.signal.aborted) setResult({ load, revision, error: apiError(error) });
      },
    );
    return () => controller.abort();
  }, [load, revision]);
  const current = result?.load === load && result.revision === revision ? result : null;
  return { ...current, loading: !current, retry: () => setRevision(n => n + 1) };
}
