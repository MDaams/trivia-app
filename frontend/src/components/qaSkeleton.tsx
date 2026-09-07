export const QASkeleton = () => {
  return (
    <div className="flex flex-row w-full h-full animate-pulse">
      <div className="flex flex-col gap-4 justify-center items-center flex-1">
        <div className="text-xl font-semibold max-w-2xl">
          <div className="h-6 bg-gray-300 rounded-md w-96"></div>
        </div>

        <div className="flex flex-col gap-2 justify-center items-center">
          <div className="w-120 px-5 py-2.5 h-10 bg-gray-300 rounded-lg"></div>
          <div className="w-120 px-5 py-2.5 h-10 bg-gray-300 rounded-lg"></div>
          <div className="w-120 px-5 py-2.5 h-10 bg-gray-300 rounded-lg"></div>
          <div className="w-120 px-5 py-2.5 h-10 bg-gray-300 rounded-lg"></div>
        </div>
      </div>
    </div>
  );
};
