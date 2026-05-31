import { useAuth } from "../contexts/AuthContext";
import { useNavigate, useParams } from "react-router-dom";
import { getRequestHistory, 
    getRequestsbyId, 
    approveRequest, 
    rejectRequest, 
    cancelRequest, } from "../services/requestService";
import { useEffect, useState } from "react";
import Navbar from "../components/Navbar";

const STATUS_LABELS = {
  PENDING:   { label: 'Pendente',  style: 'bg-yellow-100 text-yellow-700' },
  APPROVED:  { label: 'Aprovada',  style: 'bg-green-100 text-green-700' },
  REJECTED:  { label: 'Rejeitada', style: 'bg-red-100 text-red-700' },
  CANCELLED: { label: 'Cancelada', style: 'bg-gray-100 text-gray-500' },
};

export default function RequestDetail() {
    const { id } = useParams();
    const { user } = useAuth();
    const navigate = useNavigate();

    const [request, setRequest] = useState(null);
    const [history, setHistory] = useState([]);
    const [loading, setLoading] = useState(true);
    const [actionLoading, setActionLoading] = useState(false);
    const [comment, setComment] = useState('');
    const [error, setError] = useState('');

    const fetchData = async () => {
        try {
            const [reqRes, histRes] = await Promise.all([
                getRequestsbyId(id),
                getRequestHistory(id),
            ]);
            setRequest(reqRes.data);
            setHistory(histRes.data)
        } catch {
            setError("Erro ao carregar solicitação")
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        // eslint-disable-next-line react-hooks/set-state-in-effect
        fetchData();
    }, [id]);

    const handleAction = async (action) => {
        setActionLoading(true);
        setError('');
        try {
            await action(id, comment);
            await fetchData();
            setComment('');
        } catch (err) {
            console.log("Erro completo: ", err)
            console.log("response: ", err.response)
            console.log("data: ", err.response?.data);
            setError(err.response?.data?.message || "Erro ao executar ação");
        } finally {
            setActionLoading(false);
        }
    }

    const canApproveOrReject = user?.role === 'APROVADOR' || user?.role === 'ADMIN';
    const canCancel = user?.role === 'ADMIN' || (user?.role === 'SOLICITANTE' && request?.requestorName === user?.name);

    if (loading) {
        return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex items-center justify-center h-64">
          <p className="text-gray-400 text-sm">Carregando...</p>
        </div>
      </div>
    );
    }

    if (error && !request) {
        return (
      <div className="min-h-screen bg-gray-50">
        <Navbar />
        <div className="flex items-center justify-center h-64">
          <p className="text-red-500 text-sm">{error}</p>
        </div>
      </div>
    );
    }

    return (
        <div className="min-h-screen bg-gray-50">
      <Navbar />

      <div className="max-w-3xl mx-auto px-6 py-8 space-y-6">

        {/* Voltar */}
        <button
          onClick={() => navigate('/dashboard')}
          className="text-sm text-blue-600 hover:text-blue-800 transition-colors"
        >
          ← Voltar
        </button>

        {/* Dados da solicitação */}
        <div className="bg-white rounded-xl border border-gray-200 p-6 space-y-4">
          <div className="flex items-start justify-between">
            <h1 className="text-xl font-bold text-gray-800">{request.title}</h1>
            <span className={`text-xs px-2 py-1 rounded-full font-medium ${STATUS_LABELS[request.status]?.style}`}>
              {STATUS_LABELS[request.status]?.label}
            </span>
          </div>

          {request.description && (
            <p className="text-sm text-gray-600">{request.description}</p>
          )}

          <div className="grid grid-cols-2 gap-4 pt-2">
            <div>
              <p className="text-xs text-gray-400">Valor</p>
              <p className="text-sm font-medium text-gray-800">
                {request.amount.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' })}
              </p>
            </div>
            <div>
              <p className="text-xs text-gray-400">Categoria</p>
              <p className="text-sm font-medium text-gray-800">{request.category}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400">Solicitante</p>
              <p className="text-sm font-medium text-gray-800">{request.requestorName}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400">Nível requerido</p>
              <p className="text-sm font-medium text-gray-800">{request.approverLevel}</p>
            </div>
            <div>
              <p className="text-xs text-gray-400">Criado em</p>
              <p className="text-sm font-medium text-gray-800">
                {new Date(request.createdAt).toLocaleDateString('pt-BR')}
              </p>
            </div>
          </div>
        </div>

        {/* Ações — só aparecem se status for PENDING */}
        {request.status === 'PENDING' && (
          <div className="bg-white rounded-xl border border-gray-200 p-6 space-y-4">
            <h2 className="text-sm font-semibold text-gray-700">Ações</h2>

            <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Comentário opcional..."
              className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 resize-none"
              rows={3}
            />

            {error && (
              <p className="text-red-500 text-sm bg-red-50 border border-red-200 rounded-lg px-3 py-2">
                {error}
              </p>
            )}

            <div className="flex gap-3">
              {canApproveOrReject && (
                <>
                  <button
                    onClick={() => handleAction(approveRequest)}
                    disabled={actionLoading}
                    className="bg-green-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-green-700 disabled:opacity-50 transition-colors"
                  >
                    {actionLoading ? 'Aguarde...' : 'Aprovar'}
                  </button>
                  <button
                    onClick={() => handleAction(rejectRequest)}
                    disabled={actionLoading}
                    className="bg-red-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-red-700 disabled:opacity-50 transition-colors"
                  >
                    {actionLoading ? 'Aguarde...' : 'Rejeitar'}
                  </button>
                </>
              )}

              {canCancel && (
                <button
                  onClick={() => handleAction(cancelRequest)}
                  disabled={actionLoading}
                  className="bg-gray-500 text-white text-sm px-4 py-2 rounded-lg hover:bg-gray-600 disabled:opacity-50 transition-colors"
                >
                  {actionLoading ? 'Aguarde...' : 'Cancelar'}
                </button>
              )}
            </div>
          </div>
        )}

        {/* Histórico */}
        <div className="bg-white rounded-xl border border-gray-200 p-6 space-y-4">
          <h2 className="text-sm font-semibold text-gray-700">Histórico</h2>

          {history.length === 0 ? (
            <p className="text-sm text-gray-400">Nenhuma ação registrada</p>
          ) : (
            <div className="space-y-3">
              {history.map((item) => (
                <div key={item.id} className="flex gap-4 text-sm">
                  <div className="flex flex-col items-center">
                    <div className="w-2 h-2 rounded-full bg-blue-400 mt-1.5" />
                    <div className="w-px flex-1 bg-gray-200 mt-1" />
                  </div>
                  <div className="pb-4">
                    <p className="text-gray-800 font-medium">
                      {item.actorName}
                      <span className="text-gray-400 font-normal"> · {item.actorRole}</span>
                    </p>
                    <p className="text-gray-500">
                      {item.fromStatus ? `${item.fromStatus} → ` : 'Criação → '}
                      {item.toStatus}
                    </p>
                    {item.comment && (
                      <p className="text-gray-400 italic mt-1">"{item.comment}"</p>
                    )}
                    <p className="text-gray-300 text-xs mt-1">
                      {new Date(item.createdAt).toLocaleString('pt-BR')}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
    )
}

