'use client';
import { useEffect, useState } from "react";

type DashBoard = {
    name: string;
    quantity: number;
    unitPrice: number;
    totalPrice: number;
}

export default function DashBoardPage() {

    const [dashBoards, setDashBoards] = useState<DashBoard[]>([]);

    const toDateInput = (d: Date) => {
        const y = d.getFullYear();
        const m = String(d.getMonth() + 1).padStart(2, "0");
        const day = String(d.getDate()).padStart(2, "0");
        return `${y}-${m}-${day}`;
    };

    const revenue = dashBoards.reduce((acc, cur) => acc + cur.totalPrice, 0);
    const [date, setDate] = useState(
        {startDate: toDateInput(new Date()), endDate: toDateInput(new Date()),});

    useEffect(() => {
        fetch(`http://localhost:8080/api/v1/orders/dashboard?startDate=${date.startDate}&endDate=${date.endDate}`)
            .then((res) => res.json())
            .then((data) => setDashBoards(data.data));
    }, [date]);

    const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const form = new FormData(e.currentTarget);

        setDate({ startDate: String(form.get("startDate")), endDate: String(form.get("endDate")) });
    }

    return (
        <main className="mx-auto mt-8 max-w-7xl rounded-[40px] bg-white p-10">
            <div className="flex justify-between">
                <h1 className="justify-center text-2xl font-bold">대시보드</h1>
                <form className="flex items-end gap-3 m-3">
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
                </form>
            </div>
            <section className="
            rounded-xl
            border border-neutral-300
            bg-[#fffaf0]
            p-8
          ">
                <div>
                    <span className="text-3xl py-8">수익: {revenue}원</span>
                </div>
                <div>
                    <span className="text-3xl py-8">판매 목록</span>
                    <ul>
                        {dashBoards.map((dashBoard, index) => (
                            <li key={index} className="flex m-4">
                                <span className="text-xl font-bold m-2">- {dashBoard.name}</span>
                                <span className="text-l m-2">{dashBoard.unitPrice}</span>
                                <span className="text-l m-2">x</span>
                                <span className="text-l m-2">{dashBoard.quantity}</span>
                                <span className="text-l m-2">= {dashBoard.totalPrice}원</span>
                            </li>
                        ))}
                    </ul>
                </div>
            </section>
        </main>
    );
}