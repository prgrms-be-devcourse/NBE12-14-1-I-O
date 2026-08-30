"use client";

import Link from "next/link";
import { OrderList } from "@/types/OrderList";
import { useEffect, useState } from "react";
import { formatDate } from "@/utils/FormatDate";
import { Product } from "@/types/Product";
import { useRouter, useSearchParams } from "next/navigation";

const toDateInput = (d: Date) => {
    const y = d.getFullYear();
    const m = String(d.getMonth() + 1).padStart(2, "0");
    const day = String(d.getDate()).padStart(2, "0");
    return `${y}-${m}-${day}`;
};

export default function AdminOrdersPage() {

    const router = useRouter();
    const searchParams = useSearchParams();

    const saveParams = ({ sort, size }: { sort: string, size: string }) => {
        router.push(`?name=${productName}&startDate=${date.startDate}&endDate=${date.endDate}&status=${deliveryStatus}&page=${page.curPage}&size=${size || page.size}&sort=${sort || page.sort}&nameSelect=${productNameSelect}&sizeSelect=${pageSizeSelect}`);
    }

    const [searchClick, setSearchClick] = useState(false);
    const [orders, setOrders] = useState<OrderList[]>([]);
    const [productName, setProductName] = useState(searchParams.get('name') || "");
    const [date, setDate] = useState(
        {
            startDate: searchParams.get('startDate') || toDateInput(new Date()),
            endDate: searchParams.get('endDate') || toDateInput(new Date()),
        });
    const [deliveryStatus, setDeliveryStatus] = useState(searchParams.get('status') || "ORDERED");
    const [page, setPage] = useState<{
        curPage: string,
        totalPages: string,
        size: string,
        sort: string
    }>
        ({
            curPage: searchParams.get('page') || "0",
            totalPages: "0",
            size: searchParams.get('size') || "12",
            sort: searchParams.get('sort') || "DESC"
        });

    const [products, setProducts] = useState<Product[]>([]);
    const [productNameSelect, setProductNameSelect] = useState(searchParams.get('nameSelect') === 'true' || false);
    const [pageSizeSelect, setPageSizeSelect] = useState(searchParams.get('sizeSelect') === 'true' || false);

    if (Number(page.curPage) < 0) {
        setPage({ ...page, curPage: "0" });
    }

    if ((page.curPage !== "0" && page.totalPages !== "0") &&
        Number(page.curPage) > Number(page.totalPages) - 1) {
        setPage({ ...page, curPage: String(Number(page.totalPages) - 1) })
    }

    const curPage = Number(page.curPage);
    const totalPages = Number(page.totalPages);
    const PAGE_SIZE = 5;

    const currentGroup = Math.floor(curPage / PAGE_SIZE);
    const startPage = currentGroup * PAGE_SIZE;
    const endPage = Math.min(startPage + PAGE_SIZE, totalPages);

    const pageNumbers = Array.from({ length: endPage - startPage }, (_, i) => startPage + i);

    useEffect(() => {
        fetch(`${process.env.NEXT_PUBLIC_API_URL}/products`)
            .then((data) => data.json())
            .then((res) => setProducts(res.data));
    }, []);

    useEffect(() => {
        if (date.startDate > date.endDate) {
            alert("시작일이 종료일보다 늦으면 안됩니다.");
            return;
        }

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
        params.append("sort", page.sort);

        fetch(`${process.env.NEXT_PUBLIC_API_URL}/admin/orders?${params.toString()}`)
            .then((res) => res.json())
            .then((data) => {
                setOrders(data.data.content);
                setPage({
                    ...page, totalPages: data.data.totalPages
                });
            });
    }, [searchClick, page.sort, page.curPage])

    if (!productNameSelect && productName === "직접 입력") {
        setProductNameSelect(!productNameSelect);
        setProductName("");
    }

    if (!pageSizeSelect && page.size === "직접 입력") {
        setPageSizeSelect(!pageSizeSelect);
        setPage({ ...page, size: "12" });
    }

    return (
        <main className="mx-auto mt-8 max-w-7xl rounded-[40px] bg-white p-12">
            {/* 검색 영역 */}
            <section className="rounded-xl border border-neutral-300 bg-[#fffaf0] p-8">
                <div className="flex items-end gap-8">
                    {/* 상품명 */}
                    <div className="flex-1">
                        <div>
                            <label className="block text-lg font-bold">
                                상품명{productNameSelect &&
                                    <span onClick={() => setProductNameSelect(false)}
                                        className="text-sm mx-4 p-1 border-2 rounded bg-gray-100 hover:bg-gray-300">다시 선택</span>
                                }
                            </label>
                        </div>
                        {!productNameSelect &&
                            <select
                                onChange={(e) => setProductName(e.target.value)}
                                className="rounded-md mt-2 border border-neutral-300 bg-white hover:bg-gray-200 p-3"
                            >
                                <option value="">All</option>
                                {products.map((product, index) =>
                                    <option key={index} value={product.name}>
                                        {product.name.length >= 22 ? product.name.slice(0, 20) + "..." : product.name}
                                    </option>)
                                }
                                <option value="직접 입력">직접 입력</option>
                            </select>}
                        {productNameSelect &&
                            <input
                                type="text"
                                placeholder="상품명"
                                onChange={(e) =>
                                    setProductName(e.target.value)}
                                className="mt-2 w-full rounded-md border border-neutral-300 bg-white p-3"
                            />
                        }
                    </div>
                    <div className="flex-1">
                        <label className="block text-lg font-bold">
                            주문 상태
                        </label>
                        <select
                            onChange={(e) => setDeliveryStatus(e.target.value)}
                            className="rounded-md mt-2 border border-neutral-300 bg-white hover:bg-gray-200 p-3"
                            value={deliveryStatus}
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
                            className="mt-2 rounded-md border border-neutral-300 bg-white hover:bg-gray-200 p-3"
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
                            className="mt-2 rounded-md border border-neutral-300 bg-white hover:bg-gray-200 p-3"
                        />
                    </div>

                    {/* 검색 버튼 */}
                    <button
                        onClick={() => {
                            saveParams({ size: "", sort: "" });
                            setSearchClick(!searchClick)
                        }}
                        className="rounded-md bg-neutral-600 hover:bg-black px-2 py-2 text-white"
                    >
                        검색
                    </button>
                    <div className="flex-1">
                        <div>
                            <label className="block text-lg font-bold">
                                검색 개수
                            </label>
                        </div>
                        {!pageSizeSelect &&
                            <select
                                onChange={(e) => {
                                    setPage({ ...page, size: e.target.value })
                                    saveParams({ size: e.target.value, sort: "" });
                                }}
                                value={page.size}
                                className="rounded-md border border-neutral-300 bg-white hover:bg-gray-200 p-3"
                            >
                                <option value="12">12개</option>
                                <option value="18">18개</option>
                                <option value="24">24개</option>
                                <option value="30">30개</option>
                                <option value="60">60개</option>
                                <option value="90">90개</option>
                                <option value="직접 입력">직접 입력</option>
                            </select>
                        }
                        {pageSizeSelect &&
                            <input
                                type="text"
                                placeholder="검색 개수 입력"
                                onChange={(e) =>
                                    setPage({ ...page, size: e.target.value })}
                                className="mt-2 w-full rounded-md border border-neutral-300 bg-white p-3"
                            />
                        }
                    </div>

                    {/* 내림차순 올림차순 */}
                    <button onClick={() => {
                        let s = page.sort === "DESC" ? "ASC" : "DESC";
                        setPage({ ...page, sort: s })
                        saveParams({ sort: s, size: "" });
                    }}
                        className="border-1 rounded p-1 py-2 bg-gray-100 hover:bg-gray-300">{page.sort === "DESC" ? "내림차순" : "올림차순"}</button>

                </div>

                {/* 주문 목록 */}
                <div className="mt-10 grid grid-cols-3 gap-6">
                    {orders.map((order) => (
                        <article
                            key={order.orderId}
                            className="rounded-xl border border-neutral-300 bg-white p-5"
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
                                        주문자 이메일
                                    </span>
                                    <span>{order.email}</span>
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
                                className="mt-6 block rounded-md bg-neutral-800 px-4 py-2 text-center text-white"
                            >
                                상세보기
                            </Link>
                        </article>
                    ))}
                </div>
                <div className="font-bold flex justify-center m-2">
                    <button onClick={() => { setPage({ ...page, curPage: String(curPage - 1) }) }} className="text-xl mr-3">이전</button>
                    {
                        pageNumbers.map((num) =>
                            <button
                                onClick={() => setPage({ ...page, curPage: String(num) })}
                                key={num}
                                className={num === curPage ? "underline text-xl mx-3 mt-1" : "text-l mx-3 mt-1"}>
                                {num + 1}
                            </button>
                        )
                    }
                    <button onClick={() => setPage({ ...page, curPage: String(curPage + 1) })} className="text-xl ml-3">다음</button>
                </div>
            </section>
        </main>
    );
}