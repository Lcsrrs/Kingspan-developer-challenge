import { useCallback, useEffect, useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import Navbar from "../components/Navbar";
import { getRequests } from "../services/requestService";
import { useNavigate } from "react-router-dom";


const STATUS_LABELS = {
    PENDING: { label: 'Pendente', style: 'bg-yellow-100 text-yellow-700' },
    APPROVED: { label: 'Aprovado', style: 'bg-green-100 text-green-700' },
    REJECTED: { label: 'Rejeitado', style: 'bg-red-100 text-red-700' },
    CANCELLED: { label: 'Cancelado', style: 'bg-gray-100 text-gray-700' },
};

export default function Dashboard() {
    const { user } = useAuth();

    const [requests, setRequests] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState('');

    const [filterStatus, setFilterStatus] = useState('');
    const [filterCreatedBy, setFilterCreatedBy] = useState('');

    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    const navigate = useNavigate();

    const fetchRequests = useCallback(async () => {
        setLoading(true);
        setError('');
        try {
            const params = { page, size:10 };
            if (filterStatus) params.status = filterStatus;

            const res = await getRequests(params);
            let content = res.data.content;

            if (filterCreatedBy.trim()) {
                content = content.filter((r) => 
                    r.requestorName.toLowerCase().includes(filterCreatedBy.trim().toLowerCase())
                );
            }

            setRequests(content);
            setTotalPages(res.data.totalPages);
        } catch {
            setError('Erro ao carregar solicitações');
        } finally {
            setLoading(false);
        }
    }, [page, filterStatus, filterCreatedBy]);

    useEffect(() => {
        const loadRequests = async () => {
            await fetchRequests();
        };
        loadRequests();
    }, [fetchRequests]);

    const handleStatusChange = (e) => {
        setFilterStatus(e.target.value);
        setPage(0);
    };

    const handleCreatedByChange = (e) => {
        setFilterCreatedBy(e.target.value);
        setPage(0);
    };

    return (
    <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-6xl mx-auto px-6 py-8">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-xl font-bold text-gray-800">Solicitações de compra</h1>

          {user?.role === 'SOLICITANTE' && (
            <button
              onClick={() => navigate('/new-request')}
              className="bg-blue-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-blue-700 transition-colors"
            >
              Nova solicitação
            </button>
          )}
        </div>

        {/* Filtros */}
        <div className="bg-white rounded-xl border border-gray-200 p-4 mb-6 flex gap-4 flex-wrap">
          <div className="flex flex-col gap-1">
            <label className="text-xs font-medium text-gray-500">Status</label>
            <select
              value={filterStatus}
              onChange={handleStatusChange}
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <option value="">Todos</option>
              <option value="PENDING">Pendente</option>
              <option value="APPROVED">Aprovada</option>
              <option value="REJECTED">Rejeitada</option>
              <option value="CANCELLED">Cancelada</option>
            </select>
          </div>

          <div className="flex flex-col gap-1">
            <label className="text-xs font-medium text-gray-500">Criado por</label>
            <input
              type="text"
              value={filterCreatedBy}
              onChange={handleCreatedByChange}
              placeholder="Nome do solicitante"
              className="border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />
          </div>
        </div>

        {/* Tabela */}
        <div className="bg-white rounded-xl border border-gray-200 overflow-hidden">
          {loading ? (
            <div className="p-8 text-center text-sm text-gray-400">Carregando...</div>
          ) : error ? (
            <div className="p-8 text-center text-sm text-red-500">{error}</div>
          ) : requests.length === 0 ? (
            <div className="p-8 text-center text-sm text-gray-400">Nenhuma solicitação encontrada</div>
          ) : (
            <table className="w-full text-sm">
              <thead className="bg-gray-50 border-b border-gray-200">
                <tr>
                  <th className="text-left px-4 py-3 text-xs font-medium text-gray-500">Título</th>
                  <th className="text-left px-4 py-3 text-xs font-medium text-gray-500">Solicitante</th>
                  <th className="text-left px-4 py-3 text-xs font-medium text-gray-500">Valor</th>
                  <th className="text-left px-4 py-3 text-xs font-medium text-gray-500">Categoria</th>
                  <th className="text-left px-4 py-3 text-xs font-medium text-gray-500">Status</th>
                  <th className="text-left px-4 py-3 text-xs font-medium text-gray-500">Data</th>
                  <th className="text-left px-4 py-3 text-xs font-medium text-gray-500">Ações</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {requests.map((req) => (
                  <tr key={req.id} className="hover:bg-gray-50 transition-colors">
                    <td className="px-4 py-3 font-medium text-gray-800">{req.title}</td>
                    <td className="px-4 py-3 text-gray-500">{req.requestorName}</td>
                    <td className="px-4 py-3 text-gray-700">
                      {req.amount.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
                    </td>
                    <td className="px-4 py-3 text-gray-500">{req.category}</td>
                    <td className="px-4 py-3">
                      <span className={`text-xs px-2 py-1 rounded-full font-medium ${STATUS_LABELS[req.status]?.style}`}>
                        {STATUS_LABELS[req.status]?.label}
                      </span>
                    </td>
                    <td className="px-4 py-3 text-gray-400">
                      {new Date(req.createdAt).toLocaleDateString('pt-BR')}
                    </td>
                    <td className="px-4 py-3">
                      <button
                        onClick={() => navigate(`/requests/${req.id}`)}
                        className="text-blue-600 hover:text-blue-800 text-xs font-medium transition-colors"
                      >
                        Ver detalhes
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}

          {/* Paginação */}
          {!loading && totalPages > 1 && (
            <div className="flex items-center justify-between px-4 py-3 border-t border-gray-200">
              <span className="text-xs text-gray-400">Página {page + 1} de {totalPages}</span>
              <div className="flex gap-2">
                <button
                  onClick={() => setPage((p) => p - 1)}
                  disabled={page === 0}
                  className="text-xs px-3 py-1 border border-gray-300 rounded-lg disabled:opacity-40 hover:bg-gray-50 transition-colors"
                >
                  Anterior
                </button>
                <button
                  onClick={() => setPage((p) => p + 1)}
                  disabled={page + 1 >= totalPages}
                  className="text-xs px-3 py-1 border border-gray-300 rounded-lg disabled:opacity-40 hover:bg-gray-50 transition-colors"
                >
                  Próxima
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );

}