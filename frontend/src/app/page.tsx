"use client";

import ProductCardUser from "@/components/ProductCardUser";
import { Product } from "@/types/Product";
import { useEffect, useState } from "react";

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

  const [quantities, setQuantities] = useState<Record<number, number>>({});

  const [products, setProducts] = useState<Product[]>([]);

  useEffect(() => {
    fetch("http://localhost:8080/api/v1/products")
      .then((res) => res.json())
      .then((data) => setProducts(data));
  }, []);

  return (
    <main className="bg-transparent px-8 py-6">
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
        <div
          className="
          rounded-xl
          border border-neutral-300
          bg-[#fffaf0]
          px-12 py-10
          "
        >
          <h2 className="mb-10 text-center text-4xl font-bold">
            전체 목록
          </h2>

          <div className="grid grid-cols-4 gap-6">
            {products.map((product) => (
              <ProductCardUser
                key={product.id}
                product={product}
                quantities={quantities[product.id]}
                setQuantities={(value: any) => { setQuantities(value) }}
                handleClickDecrease={() => {
                  setQuantities((prev) => ({
                    ...prev,
                    [product.id]: Math.max((prev[product.id] ?? 1) - 1, 1),
                  }));
                }}
                handleClickIncrease={() => {
                  setQuantities((prev) => ({
                    ...prev,
                    [product.id]: (prev[product.id] ?? 1) + 1,
                  }));
                }}
                handleClickCartItem={() => {
                  setQuantities((prev) => ({
                    ...prev,
                    [product.id]: 1,
                  }));
                }}
              />
            ))}
          </div>
        </div>
      </section>
    </main>
  );
}