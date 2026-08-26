const products = [
    {
      id: 1,
      name: "에티오피아 예가체프",
      price: 4800,
    },
    {
      id: 2,
      name: "콜롬비아 수프리모",
      price: 4500,
    },
    {
      id: 3,
      name: "과테말라 안티구아",
      price: 5000,
    },
    {
      id: 4,
      name: "브라질 산토스",
      price: 4300,
    },
  ];

  export default function AdminPage() {
    return (
      <main className="mx-auto mt-8 max-w-7xl rounded-[40px] bg-white p-12">
        <section className="rounded-xl border bg-[#fffdf8] p-8">
          <h2 className="text-2xl font-bold">
            상품 관리
          </h2>
  
          <div className="mt-8 grid grid-cols-4 gap-6">
            {products.map((product) => (
              <article
                key={product.id}
                className="rounded-lg border bg-white p-4"
              >
                <div className="aspect-square rounded bg-neutral-100" />
  
                <h3 className="mt-4 font-bold">
                  {product.name}
                </h3>
  
                <p>
                  {product.price.toLocaleString()}원
                </p>
  
                <div className="mt-4 flex gap-2">
                  <button className="flex-1 rounded bg-neutral-600 py-2 text-white">
                    수정
                  </button>
  
                  <button className="flex-1 rounded bg-red-500 py-2 text-white">
                    삭제
                  </button>
                </div>
              </article>
            ))}
          </div>
  
          <div className="mt-10 flex justify-end">
            <button className="rounded bg-neutral-800 px-6 py-3 text-white">
              상품 추가
            </button>
          </div>
        </section>
      </main>
    );
  }