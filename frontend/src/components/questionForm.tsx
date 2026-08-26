import { Button } from "./button";
import type { TriviaQuestion } from "../types";

interface QuestionFormProps {
  triviaQuestion: TriviaQuestion;
  answerGiven: boolean;
  correctAnswerValue?: string;
  submittedAnswer?: string;
  onSelectAnswer: (id: string, value: string) => void;
  onNextQuestion: () => void;
  hasCorrectAnswer?: boolean;
}

export function QuestionForm({
  triviaQuestion,
  answerGiven,
  correctAnswerValue,
  submittedAnswer,
  onSelectAnswer,
  onNextQuestion,
  hasCorrectAnswer,
}: QuestionFormProps) {
  const { id, possibleAnswers: answers } = triviaQuestion;

  const isAnswer = (value: string): boolean => value === correctAnswerValue;
  const isSubmittedAnswer = (value: string): boolean =>
    value === submittedAnswer;

  const answerEvaluation = hasCorrectAnswer ? "Amazing!" : "Aww!";

  return (
    <div className="grid grid-rows-2">
      <div className="flex flex-col gap-8">
        <div>
          <Button
            isSubmittedAnswer={answerGiven && isSubmittedAnswer(answers[0])}
            isAnswer={answerGiven && isAnswer(answers[0])}
            disabled={answerGiven}
            onClickCallback={() => onSelectAnswer(id, answers[0])}
            className="mr-8"
          >
            {answers[0]}
          </Button>
          <Button
            isSubmittedAnswer={answerGiven && isSubmittedAnswer(answers[1])}
            isAnswer={answerGiven && isAnswer(answers[1])}
            disabled={answerGiven}
            onClickCallback={() => onSelectAnswer(id, answers[1])}
          >
            {answers[1]}
          </Button>
        </div>

        {answers.length > 2 && (
          <div>
            <Button
              isSubmittedAnswer={answerGiven && isSubmittedAnswer(answers[2])}
              isAnswer={answerGiven && isAnswer(answers[2])}
              disabled={answerGiven}
              onClickCallback={() => onSelectAnswer(id, answers[2])}
              className="mr-8"
            >
              {answers[2]}
            </Button>
            <Button
              isSubmittedAnswer={answerGiven && isSubmittedAnswer(answers[3])}
              isAnswer={answerGiven && isAnswer(answers[3])}
              disabled={answerGiven}
              onClickCallback={() => onSelectAnswer(id, answers[3])}
            >
              {answers[3]}
            </Button>
          </div>
        )}
      </div>

      {answerGiven && (
        <Button className="mt-2 h-12" onClickCallback={onNextQuestion}>
          {answerEvaluation} Next Question
        </Button>
      )}
    </div>
  );
}
