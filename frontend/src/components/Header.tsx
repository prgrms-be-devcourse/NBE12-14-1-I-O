"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import CoffeeBean from "./icons/CoffeeBean";

export default function Header() {
  const pathname = usePathname();

  const isAdmin = pathname.startsWith("/admin");

  return (
    <header className="relative flex flex-col items-center gap-6">
      <Link href="/" className="flex">
        <div className="text-amber-700 hover:text-amber-900">
          <CoffeeBean className="w-10 h-10" />
        </div>
        <h1 className="text-4xl font-bold">
          아요 Coffee
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
          <>
            <div>ㅣ</div>
            <div>
              <Link href="/admin" className="pr-2">
                상품 관리
              </Link>
              <Link href="/admin/dashboard" className="px-2">
                대시 보드
              </Link>
              <Link href="/admin/orders" className="px-2">
                주문 목록
              </Link>
            </div>
          </>
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