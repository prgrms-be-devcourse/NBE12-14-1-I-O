import Link from "next/link";

const orders = [
  {
    id: 1,
    date: "2026.08.26",
    status: "주문 완료",
    price: 10000,
    address: "서울시 강남구",
    zipCode: "12345",
  },
  {
    id: 2,
    date: "2026.08.27",
    status: "주문 완료",
    price: 20000,
    address: "서울시 송파구",
    zipCode: "05678",
  },
  {
    id: 3,
    date: "2026.08.28",
    status: "주문 완료",
    price: 30000,
    address: "서울시 마포구",
    zipCode: "04123",
  },
];

export default function OrdersPage() {
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
              key={order.id}
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
                  <span>{order.zipCode}</span>
                </div>
              </div>

              <Link
                href={`/orders/${order.id}`}
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