import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";

export default function Navbar() {
    const { user, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
    <nav className="bg-white border-b border-gray-200 px-6 py-3 flex items-center justify-between">
      <span className="font-bold text-gray-800">Kingspan</span>

      <div className="flex items-center gap-4">
        <span className="text-sm text-gray-500">{user?.name}</span>
        <span className="text-xs bg-gray-100 text-gray-600 px-2 py-1 rounded-full">{user?.role}</span>
        <button
          onClick={handleLogout}
          className="text-sm text-red-500 hover:text-red-700 transition-colors"
        >
          Sair
        </button>
      </div>
    </nav>
  );
}