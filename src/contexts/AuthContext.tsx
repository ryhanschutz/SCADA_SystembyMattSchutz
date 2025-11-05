import React, { createContext, useContext, useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { toast } from 'sonner';

export type UserRole = 'admin' | 'operador' | 'visitante';

interface User {
  id: string;
  username: string;
  role: UserRole;
  name: string;
}

interface AuthContextType {
  user: User | null;
  login: (username: string, password: string) => boolean;
  logout: () => void;
  isAuthenticated: boolean;
  canControl: boolean; // Se pode ligar/desligar equipamentos
  canModifySettings: boolean; // Se pode modificar configurações
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Usuários simulados (em produção, isso viria do backend)
const mockUsers: Array<User & { password: string }> = [
  { id: '1', username: 'admin', password: 'admin123', name: 'Administrador', role: 'admin' },
  { id: '2', username: 'operador', password: 'op123', name: 'Operador', role: 'operador' },
  { id: '3', username: 'visitante', password: 'visit123', name: 'Visitante', role: 'visitante' },
];

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(null);

  useEffect(() => {
    // Verificar se há sessão armazenada
    const storedUser = localStorage.getItem('scada_user');
    if (storedUser) {
      setUser(JSON.parse(storedUser));
    }
  }, []);

  const login = (username: string, password: string): boolean => {
    const foundUser = mockUsers.find(
      u => u.username === username && u.password === password
    );

    if (foundUser) {
      const { password: _, ...userWithoutPassword } = foundUser;
      setUser(userWithoutPassword);
      localStorage.setItem('scada_user', JSON.stringify(userWithoutPassword));
      toast.success(`Bem-vindo, ${foundUser.name}!`);
      return true;
    }

    toast.error('Credenciais inválidas');
    return false;
  };

  const logout = () => {
    setUser(null);
    localStorage.removeItem('scada_user');
    toast.info('Sessão encerrada');
  };

  const canControl = user?.role === 'admin' || user?.role === 'operador';
  const canModifySettings = user?.role === 'admin';

  return (
    <AuthContext.Provider
      value={{
        user,
        login,
        logout,
        isAuthenticated: !!user,
        canControl,
        canModifySettings,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
};
