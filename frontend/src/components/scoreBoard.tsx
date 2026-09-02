interface ScoreBoardProps {
  numberOfCorrectQuestions: number;
  numberOfTotalAnsweredQuestions: number;
}

export const ScoreBoard = ({
  numberOfCorrectQuestions,
  numberOfTotalAnsweredQuestions,
}: ScoreBoardProps) => {
  return (
    <span>
      {numberOfCorrectQuestions} questions correct out of{" "}
      {numberOfTotalAnsweredQuestions}.
    </span>
  );
};
