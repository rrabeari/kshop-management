export interface User {
  id: number;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  enabled: boolean;
  role?: string;
}

export interface UpdateUserRequest {
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  enabled: boolean;
}

export interface CreateUserRequest {
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  enabled: boolean;
}