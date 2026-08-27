export function formatDate(dateString: string) {
    if (!dateString) return "-";
    const date = new Date(dateString);
    
    const year = date.getFullYear();
    // 한 자릿수 월/일 앞에 0을 채워줍니다 (예: 08월)
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
  
    return `${year}-${month}-${day} ${hours}:${minutes}`; // ➔ "2026-08-27 12:43"
  }
  