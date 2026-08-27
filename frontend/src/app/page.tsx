"use client";

import ProductCardUser from "@/components/ProductCardUser";
import { Product } from "@/types/Product";
import { useEffect, useState } from "react";

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