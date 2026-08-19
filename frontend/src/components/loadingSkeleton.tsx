export const QASkeleton = () => {
  return (
    <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-200 w-full max-w-md animate-pulse flex flex-col items-center gap-8">
      {/* Vraag skeleton */}
      <div className="h-7 bg-gray-300 rounded-md w-3/4"></div>

      {/* Grid skeleton */}
      <div className="grid grid-cols-2 gap-4 w-full">
        <div className="h-11 bg-gray-300 rounded-lg"></div>
        <div className="h-11 bg-gray-300 rounded-lg"></div>
        <div className="h-11 bg-gray-300 rounded-lg"></div>
        <div className="h-11 bg-gray-300 rounded-lg"></div>
      </div>
    </div>
  );
};
