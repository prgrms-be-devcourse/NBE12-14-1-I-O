import Link from "next/link";

const orders = [
    {
      id: 1,
      date: "2026.08.26",
      status: "주문 완료",
      price: 10000,
    },
    {
      id: 2,
      date: "2026.08.27",
      status: "주문 완료",
      price: 20000,
    },
    {
      id: 3,
      date: "2026.08.28",
      status: "주문 완료",
      price: 30000,
    },
  ];

  export default function OrdersPage() {
    return (
      <main className="mx-auto mt-8 max-w-6xl rounded-[40px] bg-white p-12">
        <h2 className="text-3xl font-bold">주문찾기</h2>
  
        <div className="mt-8 grid grid-cols-3 gap-6">
          {orders.map((order) => (
            <article
              key={order.id}
              className="rounded-xl border p-5"
            >
              <p>주문번호: {order.id}</p>
              <p>주문날짜: {order.date}</p>
              <p>주문상태: {order.status}</p>
              <p>총 가격: {order.price.toLocaleString()}원</p>
  
              <Link
                href={`/orders/${order.id}`}
                className="mt-5 block rounded bg-neutral-800 px-4 py-2 text-center text-white"
              >
                상세보기
              </Link>
            </article>
          ))}
        </div>
      </main>
    );
  }