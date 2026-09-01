export interface TriviaQuestion {
  id: string;
  possibleAnswers: string[];
  question: string;
}

export interface TriviaQuestionDTO {
  id: string;
  possible_answers: string[];
  question: string;
}

export interface TriviaQuestionResponseDTO {
  results?: TriviaQuestionDTO[];
  error?: string;
}

export interface CheckAnswersResponseDTO {
  results?: CheckAnswersDTO[];
  error?: string;
}

export interface CheckAnswersDTO {
  id: string;
  correctAnswer: string;
  isCorrect: boolean;
}

export interface EvaluateAnswer {
  id: string;
  correctAnswer?: string;
  isCorrect?: boolean;
  error?: string;
}
