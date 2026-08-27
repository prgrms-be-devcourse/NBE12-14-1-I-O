'use client';
import ProductAddModal from "@/components/admin/ProductAddModal";
import ProductUpdateModal from "@/components/admin/ProductUpdateModal";
import ProductCard from "@/components/admin/ProductCard";
import { Product } from "@/types/Product";
import { useEffect, useState } from "react";

export default function AdminPage() {

  const [productAdd, setProductAdd] = useState(false);

  const [productUpdate, setProductUpdate] = useState(false);

  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);

  const [products, setProducts] = useState<Product[]>([]);

  useEffect(() => {
    fetch('http://localhost:8080/api/v1/products')
      .then((res) => {
        if (!res.ok) {
          throw new Error(`백엔드 조회 실패 (상태코드: ${res.status})`);
        }
        return res.json();
      })
      .then((data) => {
        if (Array.isArray(data.data)) {
          setProducts(data.data);
        } else {
          setProducts([]); 
        }
      })
      .catch((err) => {
        console.error("서버 연결 실패 또는 타입 에러:", err);
        setProducts([]);
      });
  }, [productAdd, productUpdate]);

  // 상품 삭제
  const handleDeleteProduct = async (id: number) => {
    try {
      const response = await fetch(`http://localhost:8080/api/v1/admin/products/${id}`, {
        method: 'DELETE',
      });

      if (!response.ok) {
        throw new Error('상품 삭제에 실패했습니다.');
      }

      // 백엔드 삭제 완료 후 화면 새로고침 효과
      setProducts((prev) => prev.filter((p) => p.id !== id));
      alert('성공적으로 삭제되었습니다.');
    } catch (error) {
      console.error(error);
      alert('삭제 중 오류가 발생했습니다.');
    }
  };

  const handleProductEditClick = (product: Product) => {
    setSelectedProduct(product);
    setProductUpdate(true);
  };

  const handleProductAddClick = () => {
    setProductAdd(!productAdd);
  }

  const handleProductUpdateClose = () => {
    setProductUpdate(false);
    setSelectedProduct(null);
  }

  return (
    <main className="mx-auto mt-8 max-w-7xl rounded-[40px] bg-white p-12">
      {productAdd ? <ProductAddModal onClose={handleProductAddClick} /> : undefined}
      {productUpdate && selectedProduct ? (
      <ProductUpdateModal 
        product={selectedProduct} 
        onClose={handleProductUpdateClose} 
      />
    ) : undefined}
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
            <ProductCard 
              key={product.id} 
              product={product}
              onDelete={handleDeleteProduct}
              onEdit={handleProductEditClick} 
            />
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