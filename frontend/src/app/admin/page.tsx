'use client';
import ProductAddModal from "@/components/admin/ProductAddModal";
import ProductCard from "@/components/ProductCard";
import { Product } from "@/types/product";
import { useEffect, useState } from "react";

export default function AdminPage() {

  const [productAdd, setProductAdd] = useState(false);

  const [products, setProducts] = useState<Product[]>([]);

  useEffect(() => {
    fetch('http://localhost:8080/product')
      .then((res) => res.json())
      .then((data) => {
        setProducts(data);
      });
  }, [productAdd]);

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
