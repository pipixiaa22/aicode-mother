// @ts-ignore
/* eslint-disable */
import request from '@/config/request.ts'

/** 此处后端没有提供注释 GET /test */
export async function test(options?: { [key: string]: any }) {
  return request<API.BaseResponseString>('/test', {
    method: 'GET',
    ...(options || {}),
  })
}
