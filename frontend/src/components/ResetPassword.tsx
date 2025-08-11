import React, { useState, useEffect } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import './ResetPassword.css';

interface ResetPasswordForm {
  newPassword: string;
  confirmPassword: string;
}

const ResetPassword: React.FC = () => {
  const [searchParams] = useSearchParams();
  const token = searchParams.get('token');

  const [form, setForm] = useState<ResetPasswordForm>({
    newPassword: '',
    confirmPassword: ''
  });
  const [loading, setLoading] = useState(false);
  const [validatingToken, setValidatingToken] = useState(true);
  const [tokenValid, setTokenValid] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  useEffect(() => {
    if (!token) {
      setMessage('缺少重置令牌');
      setValidatingToken(false);
      return;
    }

    validateToken(token);
  }, [token]);

  const validateToken = async (token: string) => {
    try {
      const response = await fetch(`http://localhost:8080/api/auth/validate-reset-token?token=${token}`, {
        method: 'GET',
        headers: {
          'Content-Type': 'application/json',
        },
      });

      const data = await response.json();

      if (response.ok) {
        setTokenValid(true);
      } else {
        setMessage(data.message || '無效的重置令牌');
      }
    } catch (err) {
      setMessage('連接服務器失敗');
    } finally {
      setValidatingToken(false);
    }
  };

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

    // 驗證密碼匹配
    if (form.newPassword !== form.confirmPassword) {
      setMessage('密碼不匹配');
      setLoading(false);
      return;
    }

    if (form.newPassword.length < 6) {
      setMessage('密碼至少需要6個字符');
      setLoading(false);
      return;
    }

    try {
      const response = await fetch('http://localhost:8080/api/auth/reset-password', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          token: token,
          newPassword: form.newPassword
        }),
      });

      const data = await response.json();

      if (response.ok) {
        setSuccess(true);
        setMessage(data.data || data.message);
        setForm({ newPassword: '', confirmPassword: '' });
      } else {
        setMessage(data.message || '重置密碼失敗');
      }
    } catch (err) {
      setMessage('連接服務器失敗');
    } finally {
      setLoading(false);
    }
  };

  if (validatingToken) {
    return (
      <div className="reset-password-container">
        <div className="reset-password-card">
          <div className="loading">
            驗證重置令牌...
          </div>
        </div>
      </div>
    );
  }

  if (!tokenValid) {
    return (
      <div className="reset-password-container">
        <div className="reset-password-card">
          <div className="error-state">
            <h2>令牌無效或已過期</h2>
            <p>{message}</p>
            <div className="actions">
              <Link to="/forgot-password" className="action-button">
                重新申請密碼重置
              </Link>
              <Link to="/login" className="back-to-login">
                返回登入
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (success) {
    return (
      <div className="reset-password-container">
        <div className="reset-password-card">
          <div className="success-message">
            <h2>密碼重置成功！</h2>
            <p>{message}</p>
            <div className="actions">
              <Link to="/login" className="action-button">
                立即登入
              </Link>
            </div>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="reset-password-container">
      <div className="reset-password-card">
        <h1>B2B 管理系統</h1>
        <h2>重置密碼</h2>
        <p className="description">
          請輸入您的新密碼。
        </p>
        
        {message && (
          <div className="error-message">
            {message}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label htmlFor="newPassword">新密碼</label>
            <input
              type="password"
              id="newPassword"
              name="newPassword"
              value={form.newPassword}
              onChange={handleChange}
              required
              disabled={loading}
              placeholder="輸入新密碼（至少6個字符）"
              minLength={6}
            />
          </div>

          <div className="form-group">
            <label htmlFor="confirmPassword">確認新密碼</label>
            <input
              type="password"
              id="confirmPassword"
              name="confirmPassword"
              value={form.confirmPassword}
              onChange={handleChange}
              required
              disabled={loading}
              placeholder="再次輸入新密碼"
              minLength={6}
            />
          </div>

          <button 
            type="submit" 
            className="submit-button"
            disabled={loading}
          >
            {loading ? '重置中...' : '重置密碼'}
          </button>
        </form>

        <div className="links">
          <Link to="/login">返回登入</Link>
        </div>
      </div>
    </div>
  );
};

export default ResetPassword;