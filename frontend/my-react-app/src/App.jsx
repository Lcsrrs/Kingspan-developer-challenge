import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom'
import { AuthProvider } from "./contexts/AuthContext";
import Login from './pages/Login';
import PrivateRoute from './components/PrivateRoute'
import Dashboard from './pages/Dashboard';
import RequestDetail from './pages/RequestDetails';
import NewRequest from './pages/NewRequest';

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/" element = {<Navigate to ="/login" replace />} />
          <Route path="/login" element = {<Login />} />
          <Route path="/dashboard" element = {
            <PrivateRoute>
              <Dashboard />
            </PrivateRoute>
          } />
          <Route path='/requests/:id' element={
            <PrivateRoute>
              <RequestDetail />
            </PrivateRoute>
          } />
          <Route path='new-request' element={
            <PrivateRoute>
              <NewRequest />
            </PrivateRoute>
          } />
        </Routes>    
      </AuthProvider>
    </BrowserRouter>
  )
}
