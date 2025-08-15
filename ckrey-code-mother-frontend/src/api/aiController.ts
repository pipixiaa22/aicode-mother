// @ts-ignore
/* eslint-disable */
import request from '@/config/request.ts'

/** 此处后端没有提供注释 POST /ai/chat */
export async function chat(
  // 叠加生成的Param类型 (非body参数swagger默认没有生成对象)
  params: API.chatParams,
  options?: { [key: string]: any }
) {
  return request<API.BaseResponseHtmlCodeResult>('/ai/chat', {
    method: 'POST',
    params: {
      ...params,
    },
    ...(options || {}),
  })
}
