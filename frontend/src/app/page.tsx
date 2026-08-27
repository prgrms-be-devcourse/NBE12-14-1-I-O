"use client";

import { useState } from "react";

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

type CartItem = {
  id: number;
  name: string;
  price: number;
  quantity: number;
};

export default function Home() {

  const [quantities, setQuantities] = useState<Record<number, number>>({});

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
            <article
              key={product.id}
              className="
                flex flex-col
                rounded-lg
                border border-neutral-400
                bg-white
                p-6
              "
            >
              {/* 상품 이미지 자리 */}
              <div
                className="
                  flex aspect-square
                  items-center justify-center
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

              {/* 수량 조절 + 장바구니 */}
              <div className="mt-5 flex items-center gap-4">
                {/* 수량 조절 */}
                <div
                  className="
                  flex flex-1
                  items-center justify-between
                  rounded-full
                  border border-neutral-700
                  px-4 py-2
                  "
                >
                  <button 
                  className="text-xl"
                  onClick={() => {
                    setQuantities((prev) => ({
                      ...prev,
                      [product.id]: Math.max((prev[product.id] ?? 1) - 1, 1),
                    }));
                  }}
                  >
                    -
                  </button>
                  <span className="font-bold">
                    {quantities[product.id] ?? 1}
                  </span>
                  <button 
                  className="text-xl"
                  onClick={() => {
                    setQuantities((prev) => ({
                      ...prev,
                      [product.id]: (prev[product.id] ?? 1) + 1,
                    }));
                  }}
                  >
                    +
                  </button>
                </div>

                {/* 장바구니 버튼 */}
                <button
                onClick={() => {
                  const cartItem: CartItem = {
                    id: product.id,
                    name: product.name,
                    price: product.price,
                    quantity: quantities[product.id] ?? 1,
                  };
                  const savedCart = localStorage.getItem("cart");
                  
                  const cartItems: CartItem[] = savedCart
                  ? JSON.parse(savedCart)
                  : [];

                  const existingItem = cartItems.find(
                    (item) => item.id === cartItem.id
                  );
                
                  if (existingItem) {
                    existingItem.quantity += cartItem.quantity;
                  } else {
                    cartItems.push(cartItem);
                  }
                  
                  localStorage.setItem(
                    "cart",
                    JSON.stringify(cartItems)
                  );
                }}

                  className="
                  flex h-11 w-11
                  shrink-0
                  items-center justify-center
                  rounded-full
                  bg-[#FF902A]
                  "
                >
                  <img
                    src="/images/cart-icon.png"
                    alt="장바구니 담기"
                    className="h-5 w-5"
                  />
                </button>
              </div>
            </article>
          ))}
          </div>
        </div>
      </section>
    </main>
  );
}