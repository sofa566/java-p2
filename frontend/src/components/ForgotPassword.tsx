import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import './ForgotPassword.css';

interface ForgotPasswordForm {
  email: string;
}

const ForgotPassword: React.FC = () => {
  const [form, setForm] = useState<ForgotPasswordForm>({
    email: ''
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setMessage(null);

    try {
      const response = await fetch('http://localhost:8080/api/auth/forgot-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(form),
      });

      const data = await response.json();

      if (response.ok) {
        setSuccess(true);
        setMessage(data.data || data.message);
        setForm({ email: '' });
      } else {
        setMessage(data.message || '發送重置郵件失敗');
      }
    } catch (err) {
      setMessage('連接服務器失敗');
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div className="forgot-password-container">
        <div className="forgot-password-card">
          <div className="success-message">
            <h2>郵件已發送</h2>
            <p>{message}</p>
            <div className="actions">
              <Link to="/login" className="back-to-login">
                返回登入
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="forgot-password-container">
      <div className="forgot-password-card">
        <h1>B2B 管理系統</h1>
        <h2>忘記密碼</h2>
        <p className="description">
          請輸入您的電子郵件地址，我們將向您發送密碼重置連結。
        </p>
        
        {message && (
          <div className="message">
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="email">電子郵件</label>
            <input
              type="email"
              id="email"
              name="email"
              value={form.email}
              onChange={handleChange}
              required
              disabled={loading}
              placeholder="輸入您的電子郵件地址"
            />
          </div>

          <button 
            type="submit" 
            className="submit-button"
            disabled={loading}
          >
            {loading ? '發送中...' : '發送重置連結'}
          </button>
        </form>

        <div className="links">
          <Link to="/login">返回登入</Link>
          <span>·</span>
          <Link to="/register">創建新帳號</Link>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;