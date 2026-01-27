export interface User {
  userId?: number;
  username: string;
  email: string;
  fullName?: string;
  password?: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
}

export interface LoginResponse {
  success: boolean;
  message: string;
  userId?: number;
  username?: string;
  email?: string;
  fullName?: string;
}
