export default function CheckoutPage() {
    return (
      <main className="mx-auto mt-8 flex max-w-6xl gap-12 rounded-[40px] bg-white p-14">
        <section className="flex-1 rounded-xl bg-neutral-100 p-8">
          <label className="font-bold">
            Email
          </label>
  
          <input
            className="mt-2 w-full rounded border bg-white p-3"
            placeholder="example@email.com"
          />
  
          <label className="mt-6 block font-bold">
            주소
          </label>
  
          <input
            className="mt-2 w-full rounded border bg-white p-3"
            placeholder="서울시 강남구"
          />
  
          <label className="mt-6 block font-bold">
            우편번호
          </label>
  
          <input
            className="mt-2 w-full rounded border bg-white p-3"
            placeholder="12345"
          />
  
          <p className="mt-4 text-sm text-neutral-500">
            당일 오후 2시 이후의 주문은 다음 날 배송을 시작합니다.
          </p>
  
          <button className="mt-5 w-full rounded bg-neutral-800 py-3 text-white">
            결제
          </button>
        </section>
  
        <aside className="w-80 rounded-xl bg-neutral-100 p-6">
          <h2 className="text-3xl">
            Summary
          </h2>
  
          <hr className="my-5" />
  
          <p>상품 A × 1</p>
          <p className="mt-3">상품 B × 2</p>
  
          <hr className="my-5" />
  
          <div className="flex justify-between">
            <span>총 금액</span>
            <span>10,000원</span>
          </div>
        </aside>
      </main>
    );
  }