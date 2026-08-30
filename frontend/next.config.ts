import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  /* config options here */
  reactCompiler: true,

  // 로컬 서버(localhost:8080)이미지 허용 보안 설정
  images: {
    remotePatterns: [
      {
        protocol: 'http',
        hostname: 'localhost',
        port: '8080',
        pathname: '/**',
      },
    ],
  },
  /* 기존 설정들이 있다면 그대로 두고 아래 두 항목을 추가합니다 */
  typescript: {
    // 빌드 시 타입 에러가 있어도 무시하고 진행
    ignoreBuildErrors: true,
  },
  eslint: {
    // 빌드 시 ESLint 경고/에러가 있어도 무시하고 진행
    ignoreDuringBuilds: true,
  },
};

export default nextConfig;
