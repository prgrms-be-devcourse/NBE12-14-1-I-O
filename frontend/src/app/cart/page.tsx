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
];

export default function CartPage() {
  return (
    <main className="mx-auto mt-8 flex max-w-7xl gap-8 rounded-[40px] bg-white p-12">

      {/* 왼쪽 장바구니 상품 */}
      <section className="flex-1 space-y-5">
        {cartItems.map((item) => (
          <article
            key={item.id}
            className="flex items-center gap-6 rounded-xl border bg-neutral-50 p-5"
          >
            <div className="h-28 w-28 bg-neutral-200" />

            <div className="flex-1">
              <h2 className="text-xl font-bold">
                {item.name}
              </h2>

              <p className="mt-3">
                {item.price.toLocaleString()}원 × {item.quantity}
              </p>
            </div>

            <div className="flex items-center gap-3">
              <button className="h-9 w-9 rounded border">
                -
              </button>

              <div className="flex h-10 w-12 items-center justify-center rounded bg-neutral-700 text-white">
                {item.quantity}
              </div>

              <button className="h-9 w-9 rounded border">
                +
              </button>
            </div>
          </article>
        ))}
      </section>

      {/* 오른쪽 Summary */}
      <aside className="w-80 rounded-xl bg-neutral-100 p-6">
        <h2 className="text-3xl">
          Summary
        </h2>

        <hr className="my-5" />

        <div className="flex justify-between">
          <span>상품 수량</span>
          <span>2개</span>
        </div>

        <div className="mt-4 flex justify-between">
          <span>총 금액</span>
          <span>9,300원</span>
        </div>

        <Link
          href="/checkout"
          className="mt-8 block w-full rounded bg-neutral-800 py-3 text-center text-white"
        >
          결제하러 가기
        </Link>
      </aside>

    </main>
  );
}