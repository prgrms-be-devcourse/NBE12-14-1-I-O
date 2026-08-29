'use client';
import { useEffect, useState } from "react";

type DashBoard = {
    revenueDashBoards: RevenueDashBoard[];
    soldTop3DashBoards: SoldTop3DashBoard[];
    revenueTop3DashBoards: RevenueTop3DashBoard[];
}

type RevenueDashBoard = {
    name: string;
    quantity: number;
    unitPrice: number;
    totalPrice: number;
}

type SoldTop3DashBoard = {
    name: string;
    quantity: number;
    totalPrice: number;
}

type RevenueTop3DashBoard = {
    name: string;
    quantity: number;
    totalPrice: number;
}

export default function DashBoardPage() {

    const toDateInput = (d: Date) => {
        const y = d.getFullYear();
        const m = String(d.getMonth() + 1).padStart(2, "0");
        const day = String(d.getDate()).padStart(2, "0");
        return `${y}-${m}-${day}`;
    };

    const [dashBoards, setDashBoards] = useState<DashBoard | null>(null);
    const [date, setDate] = useState(
        { startDate: toDateInput(new Date()), endDate: toDateInput(new Date()), });

    const revenueDashBoards = dashBoards !== null ? dashBoards.revenueDashBoards : [];
    const soldTop3DashBoards = dashBoards !== null ? dashBoards.soldTop3DashBoards : [];
    const revenueTop3DashBoards = dashBoards !== null ? dashBoards.revenueTop3DashBoards : [];

    const revenue = revenueDashBoards.reduce((acc, cur) => acc + cur.totalPrice, 0);

    useEffect(() => {
        fetch(`http://localhost:8080/api/v1/admin/orders/dashboard?startDate=${date.startDate}&endDate=${date.endDate}`)
            .then((res) => res.json())
            .then((data) => setDashBoards(data.data));
        console.log(dashBoards);
    }, [date]);


    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = new FormData(e.currentTarget);
        const startDate = String(form.get("startDate"));
        const endDate = String(form.get("endDate"));

        if (startDate > endDate) {
            alert("시작일이 종료일보다 늦으면 안됩니다.");
            return;
        }

        setDate({ startDate: startDate, endDate: endDate });
    }

    return (
        <main className="mx-auto mt-8 max-w-7xl rounded-[40px] bg-white p-10">
            <div className="flex justify-between">
                <h1 className="text-4xl font-bold">대시보드</h1>
                <form onSubmit={handleSubmit} className="flex flex-col items-end gap-3 m-3">
                    <div className="flex gap-2">
                        <button type="button" className="px-3 py-1 font-bold text-sm border rounded bg-[#f4e5cc] hover:bg-[#dab58a]">
                            금일 통계
                        </button>
                        <button type="button" className="px-3 py-1 font-bold text-sm border rounded bg-[#f4e5cc] hover:bg-[#dab58a]">
                            한달 통계
                        </button>
                    </div>
                    <div className="flex items-end gap-3">
                        <div className="flex items-center gap-3">
                            <label htmlFor="startDate" className="block text-sm font-medium">
                                시작일
                            </label>
                            <input
                                type="date"
                                name="startDate"
                                id="startDate"
                                defaultValue={date.startDate}
                                className="
                                rounded-md
                                border border-neutral-300
                                bg-white
                                p-3
                            "
                            />
                        </div>
                        <span className="pb-3 text-xl">
                            ~
                        </span>
                        <div className="flex items-center gap-3">
                            <label htmlFor="endDate" className="block text-sm font-medium">
                                종료일
                            </label>
                            <input
                                type="date"
                                name="endDate"
                                id="endDate"
                                defaultValue={date.endDate}
                                className="
                                rounded-md
                                border border-neutral-300
                                bg-white
                                p-3
                            "
                            />
                        </div>
                        <button type="submit" className="p-3 border rounded bg-gray-400 text-white hover:bg-gray-700">가져오기</button>
                    </div>
                </form>
            </div>
            <section className="
            rounded-xl
            border border-neutral-300
            bg-[#fffaf0]
            p-8
          ">
                <div>
                    <p className="font-bold">기간: {date.startDate} ~ {date.endDate}</p>
                    <p className="text-3xl my-2">총 수익: {revenue.toLocaleString()}원</p>
                </div>
                <div className="border-2 p-2 my-8 bg-[#f2f7f2] rounded">
                    <span className="text-3xl py-8">
                        판매 목록
                    </span>
                    <ul>
                        {revenueDashBoards.map((dashBoard, index) => (
                            <li key={index} className="flex border-2 rounded p-2 m-4 bg-white">
                                <span className="text-xl font-bold m-2">- {dashBoard.name}</span>
                                <span className="text-l m-2">{dashBoard.unitPrice.toLocaleString()}</span>
                                <span className="text-l m-2">x</span>
                                <span className="text-l m-2">{dashBoard.quantity.toLocaleString()}</span>
                                <span className="text-l m-2">= {dashBoard.totalPrice.toLocaleString()}원</span>
                            </li>
                        ))}
                    </ul>
                </div>
                <div className="border-2 p-2 my-8 bg-[#f2f7f2] rounded">
                    <span className="text-3xl py-8">가장 많이 팔린 원두 TOP 3</span>
                    <ul>
                        {soldTop3DashBoards.map((dashBoard, index) => (
                            <li key={index} className="flex border-2 rounded p-2 m-4 bg-white">
                                <span className="text-xl font-bold m-2">Top {index + 1}. {dashBoard.name}</span>
                                <span className="text-l m-2">{dashBoard.quantity.toLocaleString()}개</span>
                                <span className="text-l m-2">{dashBoard.totalPrice.toLocaleString()}원</span>
                            </li>
                        ))}
                    </ul>
                </div>
                <div className="border-2 p-2 my-8 bg-[#f2f7f2] rounded">
                    <span className="text-3xl py-8">수익이 가장 높은 원두 TOP 3</span>
                    <ul>
                        {revenueTop3DashBoards.map((dashBoard, index) => (
                            <li key={index} className="flex border-2 rounded p-2 m-4 bg-white">
                                <span className="text-xl font-bold m-2">Top {index + 1}. {dashBoard.name}</span>
                                <span className="text-l m-2">{dashBoard.quantity.toLocaleString()}개</span>
                                <span className="text-l m-2">{dashBoard.totalPrice.toLocaleString()}원</span>
                            </li>
                        ))}
                    </ul>
                </div>
            </section>
        </main>
    );
}