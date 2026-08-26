const products = [
  {
    id: 1,
    name: "에티오피아 예가체프",
    description: "꽃향과 시트러스가 어우러진 라이트 로스팅",
    price: 4800,
  },
  {
    id: 2,
    name: "콜롬비아 수프리모",
    description: "부드러운 바디감과 균형 잡힌 산미",
    price: 4500,
  },
  {
    id: 3,
    name: "과테말라 안티구아",
    description: "초콜릿과 스모키한 풍미가 어우러진 원두",
    price: 5000,
  },
  {
    id: 4,
    name: "브라질 산토스",
    description: "고소한 너트향과 낮은 산미가 특징인 원두",
    price: 4300,
  },
  {
    id: 5,
    name: "에티오피아 예가체프",
    description: "꽃향과 시트러스가 어우러진 라이트 로스팅",
    price: 4800,
  },
  {
    id: 6,
    name: "콜롬비아 수프리모",
    description: "부드러운 바디감과 균형 잡힌 산미",
    price: 4500,
  },
  {
    id: 7,
    name: "과테말라 안티구아",
    description: "초콜릿과 스모키한 풍미가 어우러진 원두",
    price: 5000,
  },
  {
    id: 8,
    name: "브라질 산토스",
    description: "고소한 너트향과 낮은 산미가 특징인 원두",
    price: 4300,
  }
];

export default function Home() {
  return (
    <main className="min-h-screen bg-[#d8ccc4] px-8 py-6">
      {/* 상품 목록 */}
      <section
        className="
          mx-auto mt-8
          max-w-7xl
          rounded-[40px]
          border border-neutral-400
          bg-white
          px-12 py-10
        "
      >
        <h2 className="mb-10 text-center text-4xl font-bold">
          전체 목록
        </h2>

        <div className="grid grid-cols-4 gap-6">
          {products.map((product) => (
            <article
              key={product.id}
              className="
                rounded-xl
                border border-neutral-200
                bg-white
                p-5
              "
            >
              {/* 상품 이미지 자리 */}
              <div
                className="
                  flex aspect-square
                  items-center justify-center
                  rounded-lg
                  bg-neutral-100
                  text-neutral-400
                "
              >
                IMAGE
              </div>

              {/* 상품 정보 */}
              <div className="mt-5">
                <h3 className="text-xl font-bold">
                  {product.name}
                </h3>

                <p className="mt-2 min-h-12 text-sm text-neutral-500">
                  {product.description}
                </p>

                <p className="mt-4 text-xl font-bold">
                  {product.price.toLocaleString()}원
                </p>
              </div>

              {/* 수량 조절 UI */}
              <div className="mt-5 flex items-center justify-center gap-3">
                <button
                  className="
                    h-9 w-9
                    rounded-md
                    border border-neutral-400
                  "
                >
                  -
                </button>

                <div
                  className="
                    flex h-9 w-12
                    items-center justify-center
                    rounded-md
                    bg-neutral-700
                    text-white
                  "
                >
                  1
                </div>

                <button
                  className="
                    h-9 w-9
                    rounded-md
                    border border-neutral-400
                  "
                >
                  +
                </button>
              </div>

              {/* 장바구니 담기 */}
              <button
                className="
                  mt-5 w-full
                  rounded-lg
                  bg-neutral-800
                  py-3
                  font-semibold
                  text-white
                "
              >
                장바구니 담기
              </button>
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}