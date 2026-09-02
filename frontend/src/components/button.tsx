interface ButtonProps {
  children: React.ReactNode;
  className?: string;
  onClickCallback: () => void;
  disabled?: boolean;
}

export const Button = ({
  children,
  className = "",
  onClickCallback,
  disabled,
}: ButtonProps) => {
  return (
    <button
      disabled={disabled}
      onClick={() => onClickCallback()}
      className={`h-12 pulse-premium min-w-100 px-5 py-2.5 text-sm font-medium border rounded-lg focus:z-10 focus:ring-2 focus:ring-blue-500 focus:outline-none transition-all duration-150 shadow-xs cursor-pointer  
  ${
    disabled
      ? "bg-gray-100 text-gray-400 border-gray-200 cursor-not-allowed shadow-none"
      : "bg-white text-gray-900 border-gray-200"
  }  
  ${className}`}
    >
      {children}
    </button>
  );
};
