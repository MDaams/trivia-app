export const QASkeleton = () => {
  return (
    <div className="flex flex-col items-center gap-6 text-center animate-pulse">
      {/* Question skeleton */}
      <div className="flex flex-col gap-2">
        <div className="h-7 bg-gray-300 rounded-md w-80"></div>
        <div className="h-7 bg-gray-300 rounded-md w-72"></div>
      </div>

      {/* Answer buttons skeleton */}
      <div className="grid grid-rows-2">
        <div className="flex flex-col gap-8">
          {/* First row of buttons */}
          <div>
            <div className="h-10 bg-gray-300 rounded-lg mr-8 inline-block w-32"></div>
            <div className="h-10 bg-gray-300 rounded-lg inline-block w-32"></div>
          </div>

          {/* Second row of buttons */}
          <div>
            <div className="h-10 bg-gray-300 rounded-lg mr-8 inline-block w-32"></div>
            <div className="h-10 bg-gray-300 rounded-lg inline-block w-32"></div>
          </div>
        </div>

        {/* Next Question button skeleton */}
        <div className="h-12 bg-gray-300 rounded-lg mt-2 w-80"></div>
      </div>
    </div>
  );
};
