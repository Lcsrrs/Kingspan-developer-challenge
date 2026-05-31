import api from './api';

export const getRequests = (params) =>  api.get('/requests', { params });
export const getRequestsbyId = (id) =>  api.get(`/requests/${id}`);
export const getRequestHistory = (id) =>  api.get(`/requests/${id}/history`);
export const approveRequest = (id, comment) =>  api.patch(`/requests/${id}/approve`, { comment });
export const rejectRequest = (id, comment) =>  api.patch(`/requests/${id}/reject`, { comment });
export const cancelRequest = (id, comment) =>  api.patch(`/requests/${id}/cancel`, { comment });
export const createRequest = (data) => api.post('/requests', data);
