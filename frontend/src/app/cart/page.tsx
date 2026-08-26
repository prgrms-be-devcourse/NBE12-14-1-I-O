import Link from "next/link";

const cartItems = [
  {
    id: 1,
    name: "에티오피아 예가체프",
    price: 4800,
    quantity: 1,
  },
  {
    id: 2,
    name: "콜롬비아 수프리모",
    price: 4500,
    quantity: 1,
  },
  {
    id: 3,
    name: "과테말라 안티구아",
    price: 5000,
    quantity: 1,
  },
];

export default function CartPage() {
  return (
    <main
      className="
        mx-auto mt-8
        flex max-w-7xl gap-10
        rounded-[40px]
        bg-white
        p-12
      "
    >
      {/* 왼쪽 상품 목록 */}
      <section className="flex-1 space-y-6">
        {cartItems.map((item) => (
          <article
            key={item.id}
            className="
              flex items-center
              rounded-xl
              border border-neutral-300
              bg-[#fffaf0]
              p-5
            "
          >
            {/* 이미지 */}
            <div
              className="
                flex h-32 w-32
                shrink-0
                items-center justify-center
                bg-neutral-200
                text-neutral-400
              "
            >
              IMAGE
            </div>

            {/* 상품 정보 */}
            <div className="ml-6 flex-1">
              <h2 className="text-xl font-bold">
                {item.name}
              </h2>

              <p className="mt-5 text-base">
                {item.price.toLocaleString()}원 × {item.quantity}
              </p>
            </div>

            {/* 수량 */}
            <div
              className="
                flex w-40
                items-center justify-between
                rounded-full
                border border-neutral-700
                px-5 py-3
              "
            >
              <button className="text-xl">
                -
              </button>

              <span className="font-bold">
                {item.quantity}
              </span>

              <button className="text-xl">
                +
              </button>
            </div>
          </article>
        ))}
      </section>

      {/* 오른쪽 Summary */}
      <aside
        className="
          w-80
          self-start
          rounded-xl
          border border-neutral-300
          bg-[#fffaf0]
          p-7
        "
      >
        <h2 className="text-4xl">
          Summary
        </h2>

        <hr className="my-5 border-neutral-400" />

        <div className="flex justify-between text-lg">
          <span>상품 수량</span>
          <span>3개</span>
        </div>

        <div className="mt-6 flex justify-between text-lg">
          <span>총 금액</span>
          <span>14,300원</span>
        </div>

        <Link
          href="/checkout"
          className="
            mt-8 block
            w-full
            rounded-md
            bg-neutral-800
            py-3
            text-center
            text-white
          "
        >
          결제하러 가기
        </Link>
      </aside>
    </main>
  );
}