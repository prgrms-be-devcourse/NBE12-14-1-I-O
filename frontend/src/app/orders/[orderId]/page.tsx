import Link from "next/link";

export default async function OrderDetailPage({
  params,
}: {
  params: Promise<{ orderId: string }>;
}) {
  const { orderId } = await params;

  // 임시 주문 데이터
  // 나중에는 GET /orders/{orderId} 결과로 교체
  const order = {
    id: orderId,
    date: "2026.08.28",
    status: "주문 완료",
    price: 14300,
    address: "서울시 강남구",
    zipCode: "12345",
    items: [
      {
        id: 1,
        name: "에티오피아 예가체프",
        quantity: 1,
        price: 4800,
      },
      {
        id: 2,
        name: "콜롬비아 수프리모",
        quantity: 1,
        price: 4500,
      },
      {
        id: 3,
        name: "과테말라 안티구아",
        quantity: 1,
        price: 5000,
      },
    ],
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
      <section
        className="
          flex min-h-[560px] flex-col
          rounded-xl
          border border-neutral-300
          bg-[#fffaf0]
          p-10
        "
      >
        {/* 주문 번호 */}
        <h2 className="text-3xl font-medium">
          주문 번호: {order.id}
        </h2>

        {/* 주문 정보 + Summary */}
        <div className="mt-8 flex gap-16">
          {/* 왼쪽 주문 정보 */}
          <section
            className="
              w-[420px]
              rounded-xl
              border border-neutral-300
              bg-white
              p-6
            "
          >
            <div className="space-y-5">
              <div className="flex justify-between">
                <span className="font-bold">
                  주문번호
                </span>
                <span>{order.id}</span>
              </div>

              <div className="flex justify-between">
                <span className="font-bold">
                  주문날짜
                </span>
                <span>{order.date}</span>
              </div>

              <div className="flex justify-between">
                <span className="font-bold">
                  주문상태
                </span>
                <span>{order.status}</span>
              </div>

              <div className="flex justify-between">
                <span className="font-bold">
                  총 가격
                </span>
                <span>
                  {order.price.toLocaleString()}원
                </span>
              </div>

              <div className="flex justify-between gap-6">
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

                <span>
                  {order.zipCode}
                </span>
              </div>
            </div>
          </section>

          {/* 오른쪽 Summary */}
          <aside
            className="
              ml-auto
              w-[380px]
              self-start
              rounded-xl
              border border-neutral-300
              bg-white
              p-7
            "
          >
            <h2 className="text-4xl">
              Summary
            </h2>

            <hr className="my-5 border-neutral-400" />

            <div className="space-y-4">
              {order.items.map((item) => (
                <div
                  key={item.id}
                  className="flex justify-between gap-6"
                >
                  <span>
                    {item.name} × {item.quantity}
                  </span>

                  <span className="shrink-0">
                    {(item.price * item.quantity).toLocaleString()}원
                  </span>
                </div>
              ))}
            </div>

            <hr className="my-6 border-neutral-400" />

            <div className="flex justify-between text-lg">
              <span>총 금액</span>

              <span>
                {order.price.toLocaleString()}원
              </span>
            </div>
          </aside>
        </div>

        {/* 하단 버튼 */}
        <div className="mt-auto flex justify-end gap-4 pt-10">
          <button
            className="
              rounded-md
              bg-red-500
              px-6 py-3
              text-white
            "
          >
            주문 취소
          </button>

          <button
            className="
              rounded-md
              bg-neutral-800
              px-6 py-3
              text-white
            "
          >
            배송지 변경
          </button>

          <Link
            href="/orders"
            className="
              rounded-md
              bg-neutral-700
              px-6 py-3
              text-white
            "
          >
            닫기
          </Link>
        </div>
      </section>
    </main>
  );
}