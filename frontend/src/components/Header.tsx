"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

export default function Header() {
  const pathname = usePathname();

  const isAdmin = pathname.startsWith("/admin");

  return (
    <header className="relative flex flex-col items-center gap-6">
      <Link href="/">
        <h1 className="text-4xl font-bold">
          카페이름
        </h1>
      </Link>

      <nav className="flex gap-6 rounded-full bg-white px-10 py-3">
        <Link href="/">
          상품목록
        </Link>

        <Link href="/cart">
          장바구니
        </Link>

        <Link href="/orders">
          주문찾기
        </Link>

        {isAdmin && (
          <Link href="/admin">
            상품 관리
          </Link>
        )}
      </nav>

      <Link
        href="/admin"
        className="absolute right-0 top-0 rounded-md bg-neutral-800 px-12 py-2 text-white"
      >
        관리자
      </Link>
    </header>
  );
}