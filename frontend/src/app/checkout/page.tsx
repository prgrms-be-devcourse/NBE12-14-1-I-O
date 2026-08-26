export default function CheckoutPage() {
    return (
      <main
        className="
          mx-auto mt-8
          flex max-w-7xl
          gap-12
          rounded-[40px]
          bg-white
          p-12
        "
      >
        {/* 왼쪽 주문 정보 입력 */}
        <section
          className="
            flex-1
            rounded-xl
            border border-neutral-300
            bg-[#fffaf0]
            p-8
          "
        >
          {/* Email */}
          <div>
            <label className="text-lg font-bold">
              Email
            </label>
  
            <input
              className="
                mt-2 w-full
                rounded-md
                border border-neutral-300
                bg-white
                p-3
                outline-none
              "
              placeholder="example@email.com"
            />
          </div>
  
          {/* 주소 */}
          <div className="mt-6">
            <label className="text-lg font-bold">
              주소
            </label>
  
            <input
              className="
                mt-2 w-full
                rounded-md
                border border-neutral-300
                bg-white
                p-3
                outline-none
              "
              placeholder="서울시 강남구"
            />
          </div>
  
          {/* 우편번호 */}
          <div className="mt-6">
            <label className="text-lg font-bold">
              우편번호
            </label>
  
            <input
              className="
                mt-2 w-full
                rounded-md
                border border-neutral-300
                bg-white
                p-3
                outline-none
              "
              placeholder="12345"
            />
          </div>
  
          <p className="mt-4 text-sm text-neutral-500">
            당일 오후 2시 이후의 주문은 다음 날 배송을 시작합니다.
          </p>
  
          <button
            className="
              mt-5 w-full
              rounded-md
              bg-neutral-800
              py-3
              text-white
            "
          >
            결제
          </button>
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
  
          <div className="space-y-4">
            <div className="flex justify-between">
              <span>에티오피아 예가체프 × 1</span>
              <span>4,800원</span>
            </div>
  
            <div className="flex justify-between">
              <span>콜롬비아 수프리모 × 1</span>
              <span>4,500원</span>
            </div>
  
            <div className="flex justify-between">
              <span>과테말라 안티구아 × 1</span>
              <span>5,000원</span>
            </div>
          </div>
  
          <hr className="my-6 border-neutral-400" />
  
          <div className="flex justify-between text-lg">
            <span>총 금액</span>
            <span>14,300원</span>
          </div>
        </aside>
      </main>
    );
  }