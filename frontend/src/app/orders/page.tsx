"use client";

import Link from "next/link";
import {OrderList} from "@/types/OrderList";
import {useState} from "react";

export default function OrdersPage() {
  const [orders, setOrders] = useState<OrderList[]>([]);
  const [email, setEmail] = useState("");
  const handleSearch = async () => {
    if (!email.trim()) {
      alert("이메일을 입력해주세요.")
      return;
    }
    const res = await fetch(
        `http://localhost:8080/api/v1/orders?email=${encodeURIComponent(email)}`
    );
    const data = await res.json();
    setOrders(data.data);
  };
  return (
    <main
      className="
        mx-auto mt-8
        max-w-7xl
        rounded-[40px]
        bg-white
        p-12
      "
    >
      {/* 검색 영역 */}
      <section
        className="
          rounded-xl
          border border-neutral-300
          bg-[#fffaf0]
          p-8
        "
      >
        <div className="flex items-end gap-8">
          {/* 이메일 */}
          <div className="flex-1">
            <label className="text-lg font-bold">
              Email
            </label>

            <input
              type="email"
              placeholder="example@email.com"
              value={email}
              onChange={(e)=>
                  setEmail(e.target.value)}
              className="
                mt-2 w-full
                rounded-md
                border border-neutral-300
                bg-white
                p-3
              "
            />
          </div>

          {/* 시작 날짜 */}
          <div>
            <label className="block text-sm font-medium">
              시작일
            </label>

            <input
              type="date"
              className="
                mt-2 rounded-md
                border border-neutral-300
                bg-white
                p-3
              "
            />
          </div>

          <span className="pb-3 text-xl">
            ~
          </span>

          {/* 종료 날짜 */}
          <div>
            <label className="block text-sm font-medium">
              종료일
            </label>

            <input
              type="date"
              className="
                mt-2 rounded-md
                border border-neutral-300
                bg-white
                p-3
              "
            />
          </div>

          {/* 검색 버튼 */}
          <button
              onClick={handleSearch}
            className="
              rounded-md
              bg-neutral-800
              px-6 py-3
              text-white
            "
          >
            검색
          </button>
        </div>

        {/* 주문 목록 */}
        <div className="mt-10 grid grid-cols-3 gap-6">
          {orders.map((order) => (
            <article
              key={order.orderId}
              className="
                rounded-xl
                border border-neutral-300
                bg-white
                p-5
              "
            >
              <div className="space-y-2">
                <div className="flex justify-between">
                  <span className="font-bold">
                    주문번호
                  </span>
                  <span>{order.orderId}</span>
                </div>

                <div className="flex justify-between">
                  <span className="font-bold">
                    주문날짜
                  </span>
                  <span>{order.orderedAt}</span>
                </div>

                <div className="flex justify-between">
                  <span className="font-bold">
                    주문상태
                  </span>
                  <span>{order.deliveryStatus}</span>
                </div>

                <div className="flex justify-between">
                  <span className="font-bold">
                    총 가격
                  </span>
                  <span>
                    {order.price.toLocaleString()}원
                  </span>
                </div>

                <div className="flex justify-between gap-4">
                  <span className="shrink-0 font-bold">
                    주소
                  </span>
                  <span className="text-right">
                    {order.address}
                  </span>
                </div>

                <div className="flex justify-between">
                  <span className="font-bold">
                    우편번호
                  </span>
                  <span>{order.postalCode}</span>
                </div>
              </div>

              <Link
                href={`/orders/${order.orderId}`}
                className="
                  mt-6 block
                  rounded-md
                  bg-neutral-800
                  px-4 py-2
                  text-center
                  text-white
                "
              >
                상세보기
              </Link>
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}