import { Navigate } from 'react-router-dom'
import { useAuth } from "../contexts/AuthContext";

export default function PrivateRoute({ children }) {
    const { user, loading } = useAuth();

    if (loading) {
        return (
            <div className="min-h-screen flex items-center justify-center">
                <p className="text-gray-500 text-sm">Carregando...</p>
            </div>
        );
    }

    return user ? children : <Navigate to="/login" replace />;
}