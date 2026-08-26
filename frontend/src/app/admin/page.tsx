'use client';
import ProductAddModal from "@/components/admin/ProductAddModal";
import ProductCard from "@/components/ProductCard";
import { useEffect, useState } from "react";

const products = [
  {
    id: 1,
    name: "에티오피아 예가체프",
    price: 4800,
    stock: 13,
  },
  {
    id: 2,
    name: "콜롬비아 수프리모",
    price: 4500,
    stock: 14,
  },
  {
    id: 3,
    name: "과테말라 안티구아",
    price: 5000,
    stock: 15,
  },
  {
    id: 4,
    name: "브라질 산토스",
    price: 4300,
    stock: 16,
  },
];

type Product = {
  id: number,
  name: string,
  price: number,
  stock: number
};

export default function AdminPage() {

  const [productAdd, setProductAdd] = useState(false);

  const [products, setProducts] = useState<Product[]>([]);

  useEffect(() => {
    fetch('http://localhost:8080/product')
      .then((res) => res.json())
      .then((data) => {
        setProducts(data);
      });
  }, []);

  const handleProductAddClick = () => {
    setProductAdd(!productAdd);
  }

  return (
    <main className="mx-auto mt-8 max-w-7xl rounded-[40px] bg-white p-12">
      {productAdd ? <ProductAddModal onClose={handleProductAddClick} /> : undefined}
      <section className="
            rounded-xl
            border border-neutral-300
            bg-[#fffaf0]
            p-8
          ">
        <h2 className="text-2xl font-bold">
          상품 관리
        </h2>

        <div className="mt-8 grid grid-cols-4 gap-6">
          {products.map((product) => (
            <ProductCard key={product.id} product={product}/>
          ))}
        </div>

        <div className="mt-10 flex justify-end">
          <button onClick={handleProductAddClick} className="rounded bg-neutral-800 px-6 py-3 text-white">
            상품 추가
          </button>
        </div>
      </section>
    </main>
  );
}
