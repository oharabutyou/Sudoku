
import numpy as np
import copy

BOARD_SIZE = 4
BOARD_NUM = BOARD_SIZE**2
BOARD_INDEXSIZE = BOARD_SIZE**4
# sudoku_board = np.zeros(shape=(BOARD_INDEXSIZE), dtype=int)
sudoku_board = [
0,0,11,0,2,0,7,0,0,0,0,0,0,13,6,4,
3,0,2,0,0,0,0,0,0,13,0,0,12,15,0,0,
1,14,15,0,3,0,0,13,9,0,7,0,0,5,10,0,
4,0,0,9,6,12,0,10,0,14,0,0,7,0,0,0,
0,0,0,0,0,9,0,0,0,0,0,0,0,8,13,0,
12,0,0,0,14,15,0,5,0,4,8,6,0,0,0,0,
14,3,0,0,0,0,0,6,0,0,5,0,1,9,0,11,
0,6,8,15,0,0,2,1,0,12,0,0,0,0,7,0,
10,7,0,0,0,13,4,11,0,6,16,0,15,0,0,0,
2,0,12,5,0,0,0,0,8,9,0,0,0,0,14,6,
16,1,9,0,0,0,8,14,12,3,0,0,0,0,0,0,
0,0,0,0,1,2,5,0,0,0,13,0,0,4,0,9,
0,0,14,0,13,0,0,0,0,0,3,0,5,0,4,0,
0,0,0,0,0,0,0,4,16,7,0,0,0,0,0,15,
5,0,0,16,0,0,0,2,0,0,0,0,3,10,0,0,
0,15,0,0,8,0,9,0,14,0,10,0,0,0,11,0
    ]

def load():
    pass


def addNum(board, index):
    # if all index passed, board is solved and return true
    if index >= BOARD_INDEXSIZE:
        global sudoku_board
        sudoku_board = board
        return True
    # skip finding loaded number
    if board[index] != 0:
        return addNum(board, index+1)
    # copy to memory present state
    cpy = copy.copy(board)
    # print(cpy)
    # try to put num
    for num in range(1, BOARD_NUM+1):
        cpy[index] = num
        if checkIf(cpy, index) and addNum(cpy, index+1):
            # break chain when solution is found
            return True
    # return false when no solution found
    return False


def getColMember(index):
    return range(index % BOARD_NUM, BOARD_INDEXSIZE, BOARD_NUM)


def getRowMember(index):
    return range(index - index % BOARD_NUM, index - index % BOARD_NUM + BOARD_NUM)


def getMassMember(index):
    member = []
    massCol = int(index % BOARD_NUM / BOARD_SIZE)
    massRow = int(index / BOARD_NUM / BOARD_SIZE)
    leftUp = massCol * BOARD_SIZE + massRow * BOARD_SIZE * BOARD_NUM
    for row in range(BOARD_SIZE):
        member.extend(range(leftUp + row * BOARD_NUM, leftUp + row * BOARD_NUM + BOARD_SIZE))
    return member


def check(added, index):
    check_sum = 0
    # check row
    for row in getRowMember(index):
        check_sum += (row != index) * (added[row] == added[index])
    # check col
    for col in getColMember(index):
        check_sum += (col != index) * (added[col] == added[index])
    # check mass
    for mass in getMassMember(index):
        check_sum += (mass != index) * (added[mass] == added[index])
    # throughing all check means ok
    return check_sum == 0

def checkIf(added,index):
    # check row
    for row in getRowMember(index):
        if (row != index) and (added[row] == added[index]):
            return False
    # check col
    for col in getColMember(index):
        if (col != index) and (added[col] == added[index]):
            return False
    # check mass
    for mass in getMassMember(index):
        if (mass != index) and (added[mass] == added[index]):
            return False
    # throughing all check means ok
    return True

def printBoard(board):
    string = ""
    for row in range(BOARD_NUM):
        for col in range(BOARD_NUM):
            string += (str(board[row*BOARD_NUM+col])+",")
        string += "\n"
    print(string)


if __name__ == "__main__":
    print(addNum(sudoku_board, 0))
    printBoard(sudoku_board)
