import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

interface User {
  id: number;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role: string;
}

const Dashboard: React.FC = () => {
  const navigate = useNavigate();
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchUserProfile();
  }, []);

  const fetchUserProfile = async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        navigate('/login');
        return;
      }

      const response = await fetch('http://localhost:8080/api/auth/me', {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      if (response.ok) {
        const data = await response.json();
        setUser(data.data);
      } else if (response.status === 401) {
        localStorage.removeItem('token');
        navigate('/login');
      } else {
        setError('無法載入使用者資料');
      }
    } catch (err) {
      setError('連接服務器失敗');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="loading">載入中...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-container">
        <div className="error-message">{error}</div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <div className="header-content">
          <h1>B2B 管理系統</h1>
          <div className="user-info">
            <span>歡迎, {user?.firstName} {user?.lastName}</span>
            <button onClick={handleLogout} className="logout-button">
              登出
            </button>
          </div>
        </div>
      </header>

      <main className="dashboard-main">
        <div className="dashboard-content">
          <div className="welcome-section">
            <h2>儀表板</h2>
            <p>歡迎使用 B2B 管理系統！</p>
          </div>

          <div className="stats-grid">
            <div className="stat-card">
              <h3>使用者資訊</h3>
              <div className="stat-details">
                <p><strong>使用者名稱:</strong> {user?.username}</p>
                <p><strong>電子郵件:</strong> {user?.email}</p>
                <p><strong>角色:</strong> {user?.role}</p>
              </div>
            </div>

            <div className="stat-card">
              <h3>系統狀態</h3>
              <div className="stat-details">
                <p><strong>狀態:</strong> <span className="status-online">在線</span></p>
                <p><strong>最後登入:</strong> 剛剛</p>
              </div>
            </div>

            <div className="stat-card">
              <h3>快速操作</h3>
              <div className="quick-actions">
                <button className="action-button">管理店鋪</button>
                <button className="action-button">查看帳單</button>
                <button className="action-button">設定</button>
              </div>
            </div>

            <div className="stat-card">
              <h3>最近活動</h3>
              <div className="stat-details">
                <p>• 成功登入系統</p>
                <p>• 載入儀表板</p>
                <p>• 系統運行正常</p>
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;