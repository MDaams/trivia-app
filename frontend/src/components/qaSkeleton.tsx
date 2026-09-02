export const QASkeleton = () => {
  return (
    <div className="grid grid-rows-3 h-80 gap-0 justify-items-center animate-pulse">
      {/* Question skeleton */}
      <div className="flex items-center">
        <div className="h-6 bg-gray-300 rounded-md w-96"></div>
      </div>

      {/* Answer buttons skeleton */}
      <div className="flex flex-col gap-2 justify-center items-center w-full">
        <div>
          <div className="h-10 bg-gray-300 rounded-lg mr-8 inline-block w-32"></div>
          <div className="h-10 bg-gray-300 rounded-lg inline-block w-32"></div>
        </div>

        <div>
          <div className="h-10 bg-gray-300 rounded-lg mr-8 inline-block w-32"></div>
          <div className="h-10 bg-gray-300 rounded-lg inline-block w-32"></div>
        </div>
      </div>

      {/* Next button skeleton */}
      <div className="flex items-center">
        <div className="h-12 bg-gray-300 rounded-lg w-48 invisible"></div>
      </div>
    </div>
  );
};
