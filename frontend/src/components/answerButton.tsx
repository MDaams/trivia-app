interface AnswerButtonProps {
  children: React.ReactNode;
  className?: string;
  onClickCallback: () => void;
  disabled?: boolean;
  isAnswer: boolean;
  isSubmittedAnswer: boolean;
}

export const AnswerButton = ({
  children,
  className = "",
  onClickCallback,
  disabled,
  isAnswer,
  isSubmittedAnswer,
}: AnswerButtonProps) => {
  return (
    <button
      disabled={disabled}
      onClick={() => onClickCallback()}
      className={`pulse-subtle min-w-100 px-5 py-2.5 text-sm font-medium border rounded-lg focus:z-10 focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all duration-150 shadow-xs cursor-pointer  
  ${
    disabled
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
      : "bg-white text-gray-900 border-gray-200"
  }  
  ${className}`}
    >
      {children}
    </button>
  );
};
