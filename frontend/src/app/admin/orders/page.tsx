"use client";

import Link from "next/link";
import { OrderList } from "@/types/OrderList";
import { useState } from "react";
import { formatDate } from "@/utils/FormatDate";

const toDateInput = (d: Date) => {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${y}-${m}-${day}`;
};

export default function AdminOrdersPage() {
    const [orders, setOrders] = useState<OrderList[]>([]);
    const [productName, setProductName] = useState("");
    const [date, setDate] = useState(
        { startDate: toDateInput(new Date()), endDate: toDateInput(new Date()), });
    const [deliveryStatus, setDeliveryStatus] = useState("ORDERED");
    const [page, setPage] = useState<{ curPage: string, totalPages: string, size: string }>({ curPage: "0", totalPages: "0", size: "12" });

    const handleSearch = async () => {
        if ((date.startDate && !date.endDate) || (!date.startDate && date.endDate)) {
            alert("시작일과 종료일을 모두 입력해주세요.");
            return;
        }

        if (Number(page.size) <= 0) {
            alert("페이지 사이즈를 1이상으로 설정해주세요.");
            return;
        }

        const params = new URLSearchParams({
            name: productName,
        });

        params.append("startDate", date.startDate);
        params.append("endDate", date.endDate);
        params.append("status", deliveryStatus);
        params.append("page", page.curPage);
        params.append("size", page.size);

        const res = await fetch(
            `http://localhost:8080/api/v1/admin/orders?${params.toString()}`
        );
        const data = await res.json();
        console.log(data);
        setOrders(data.data.content);
        setPage({
            ...page, totalPages: data.data.totalPages
        });
        console.log(data.data);
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
                    {/* 상품명 */}
                    <div className="flex-1">
                        <label className="block text-lg font-bold">
                            상품명
                        </label>

                        <input
                            type="text"
                            placeholder="상품명"
                            value={productName}
                            onChange={(e) =>
                                setProductName(e.target.value)}
                            className="
                mt-2 w-full
                rounded-md
                border border-neutral-300
                bg-white
                p-3
              "
                        />
                    </div>
                    <div className="flex-1">
                        <label className="block text-lg font-bold">
                            주문 상태
                        </label>
                        <select
                            onChange={(e) => setDeliveryStatus(e.target.value)}
                            className="
                                rounded-md
                                mt-2
                                border border-neutral-300
                                bg-white
                                hover:bg-gray-200
                                p-3
                            "
                        >
                            <option value="ORDERED">주문 완료</option>
                            <option value="SHIPPING">배송 중</option>
                            <option value="DELIVERED">배송 완료</option>
                            <option value="CANCELLED">주문 취소</option>
                        </select>
                    </div>

                    {/* 시작 날짜 */}
                    <div>
                        <label className="block text-lg font-bold">
                            시작일
                        </label>

                        <input
                            type="date"
                            value={date.startDate}
                            onChange={(e) => setDate({ ...date, startDate: e.target.value })}
                            className="
                mt-2 rounded-md
                border border-neutral-300
                bg-white
                hover:bg-gray-200
                p-3
              "
                        />
                    </div>

                    <span className="pb-3 text-xl">
                        ~
                    </span>

                    {/* 종료 날짜 */}
                    <div>
                        <label className="block text-lg font-bold">
                            종료일
                        </label>

                        <input
                            type="date"
                            value={date.endDate}
                            onChange={(e) => setDate({ ...date, endDate: e.target.value })}
                            className="
                mt-2 rounded-md
                border border-neutral-300
                bg-white
                hover:bg-gray-200
                p-3
              "
                        />
                    </div>

                    {/* 검색 버튼 */}
                    <button
                        onClick={handleSearch}
                        className="
              rounded-md
              bg-neutral-600
              hover:bg-black
              px-6 py-3
              text-white
            "
                    >
                        검색
                    </button>
                    <div>
                        <select
                            onChange={(e) => setPage({...page, size: e.target.value})}
                            className="
                                rounded-md
                                border border-neutral-300
                                bg-white
                                hover:bg-gray-200
                                p-3
                            "
                        >
                            <option value="12">12개</option>
                            <option value="18">18개</option>
                            <option value="24">24개</option>
                            <option value="30">30개</option>
                            <option value="60">60개</option>
                            <option value="90">90개</option>
                        </select>
                    </div>
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
                                    <span>{formatDate(order.orderedAt)}</span>
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