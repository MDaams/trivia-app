export const QASkeleton = () => {
  return (
    <div className="grid grid-rows-3 h-120 gap-2 justify-items-center animate-pulse">
      <div className="text-xl font-semibold flex items-center">
        <div className="h-6 bg-gray-300 rounded-md w-96"></div>
      </div>

      <div className="flex flex-col gap-2 justify-center items-center w-full">
        <div className="min-w-100 px-5 py-2.5 h-10 bg-gray-300 rounded-lg"></div>
        <div className="min-w-100 px-5 py-2.5 h-10 bg-gray-300 rounded-lg"></div>
        <div className="min-w-100 px-5 py-2.5 h-10 bg-gray-300 rounded-lg"></div>
        <div className="min-w-100 px-5 py-2.5 h-10 bg-gray-300 rounded-lg"></div>
      </div>

      <div className="flex items-center">
        <div className="mt-8 w-120 h-10 bg-gray-300 rounded-lg"></div>
      </div>
    </div>
  );
};
