interface ButtonProps {
  children: React.ReactNode;
  className?: string;
  onClickCallback: () => void;
  disabled?: boolean;
  isAnswer?: boolean;
  isSubmittedAnswer?: boolean;
}

export const Button = ({
  children,
  className = "",
  onClickCallback,
  disabled,
  isAnswer,
  isSubmittedAnswer,
}: ButtonProps) => {
  return (
    <button
      disabled={disabled}
      onClick={() => onClickCallback()}
      className={`min-w-100 px-5 py-2.5 text-sm font-medium border rounded-lg focus:z-10 focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all duration-150 shadow-xs cursor-pointer  
  ${
    disabled && isAnswer === undefined
      ? "bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed shadow-none"
      : disabled && isAnswer !== undefined
        ? isAnswer
          ? `bg-green-100 text-green-800 cursor-default ${
              isSubmittedAnswer
                ? "border-2 border-green-300 font-semibold"
                : "border-green-200"
            }`
          : `bg-red-100 text-red-800 cursor-default ${
              isSubmittedAnswer
                ? "border-2 border-red-300 font-semibold"
                : "border-red-200"
            }`
        : "bg-white text-gray-900 border-gray-200 hover:bg-gray-100 hover:text-blue-600"
  }  
  ${className}`}
    >
      {children}
    </button>
  );
};
